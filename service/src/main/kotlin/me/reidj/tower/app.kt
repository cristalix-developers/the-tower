package me.reidj.tower

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.reidj.tower.booster.BoosterInfo
import me.reidj.tower.protocol.*
import ru.cristalix.core.CoreApi
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.permissions.PermissionService
import java.util.*

private val globalBoosters = mutableListOf<BoosterInfo>()

fun main() {
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(4))

    val mongoAdapter = MongoAdapter(System.getenv("db_url"), System.getenv("db_data"), "userData")

    ISocketClient.get().run {
        capabilities(
            LoadUserPackage::class,
            BulkSaveUserPackage::class,
            SaveUserPackage::class,
            TopPackage::class,
            ChangeRankPackage::class,
        )

        CoreApi.get().registerService(IPermissionService::class.java, PermissionService(this))

        addListener(LoadUserPackage::class.java) { realmId, pckg ->
            mongoAdapter.find(pckg.uuid).get().run {
                pckg.stat = this
                forward(realmId, pckg)
                println("Loaded on ${realmId.realmName}! Player: ${pckg.uuid}")
            }
        }
        addListener(SaveUserPackage::class.java) { realmId, pckg ->
            mongoAdapter.save(pckg.stat)
            println("Received SaveUserPackage from ${realmId.realmName} for ${pckg.uuid}")

        }
        addListener(BulkSaveUserPackage::class.java) { realmId, pckg ->
            mongoAdapter.save(pckg.packages.map { it.stat })
            println("Received BulkSaveUserPackage from ${realmId.realmName}")
        }
        addListener(TopPackage::class.java) { realmId, pckg ->
            CoroutineScope(Dispatchers.IO).launch {
                val top = mongoAdapter.getTop(pckg.topType, pckg.limit, pckg.isSortAscending)
                pckg.entries = top
                forward(realmId, pckg)
                println("Top generated for ${realmId.realmName}")
            }
        }
        addListener(ChangeRankPackage::class.java) { _, pckg ->
            CoroutineScope(Dispatchers.IO).launch {
                val stat = mongoAdapter.find(pckg.uuid).await() ?: return@launch
                stat.rank =
                    if (pckg.isSortAscending) stat.rank.downgradeRank()
                        ?: return@launch else stat.rank.upgradeRank() ?: return@launch
                stat.tournamentMaximumWavePassed = 0
                mongoAdapter.save(stat)
            }
        }
        addListener(RequestGlobalBoostersPackage::class.java) { realm, pckg ->
            pckg.boosters = globalBoosters
            forward(realm, pckg)
        }
        addListener(SaveGlobalBoosterPackage::class.java) { _, pckg ->
            globalBoosters.add(pckg.booster)
        }
    }

    runBlocking {
        val command = readLine()
        if (command!!.startsWith("delete")) {
            val args = command.split(" ")
            val uuid = UUID.fromString(args[1])
            mongoAdapter.clear(uuid)
            println("Removed $uuid's data from db...")
        }
    }
}
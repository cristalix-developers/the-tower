package me.reidj.tower

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
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

        listen<LoadUserPackage> { realmId, pckg ->
            withContext(Dispatchers.IO) { mongoAdapter.find(pckg.uuid).get() }.run {
                pckg.stat = this
                forward(realmId, pckg)
                println("Loaded on ${realmId.realmName}! Player: ${pckg.uuid}")
            }
        }
        listen<SaveUserPackage> { realmId, pckg ->
            mongoAdapter.save(pckg.stat)
            println("Received SaveUserPackage from ${realmId.realmName} for ${pckg.uuid}")

        }
        listen<BulkSaveUserPackage> { realmId, pckg ->
            mongoAdapter.save(pckg.packages.map { it.stat })
            println("Received BulkSaveUserPackage from ${realmId.realmName}")
        }
        listen<TopPackage> { realmId, pckg ->
            CoroutineScope(Dispatchers.IO).launch {
                val top = mongoAdapter.getTop(pckg.topType, pckg.limit, pckg.isSortAscending)
                pckg.entries = top
                forward(realmId, pckg)
                println("Top generated from ${realmId.realmName}")
            }
        }
        listen<ChangeRankPackage> { _, pckg ->
            val stat = mongoAdapter.find(pckg.uuid).await() ?: return@listen
            stat.rank =
                if (pckg.isSortAscending) stat.rank.downgradeRank() ?: return@listen else stat.rank.upgradeRank() ?: return@listen
            stat.tournamentMaximumWavePassed = 0
            mongoAdapter.save(stat)
        }
        listen<RequestGlobalBoostersPackage> { realm, pckg ->
            pckg.boosters = globalBoosters
            forward(realm, pckg)
        }
        listen<SaveGlobalBoosterPackage> { _, pckg ->
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
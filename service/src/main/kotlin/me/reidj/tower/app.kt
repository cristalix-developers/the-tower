package me.reidj.tower

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.reidj.tower.protocol.BulkSaveUserPackage
import me.reidj.tower.protocol.LoadUserPackage
import me.reidj.tower.protocol.SaveUserPackage
import me.reidj.tower.protocol.TopPackage
import ru.cristalix.core.CoreApi
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.permissions.PermissionService

fun main() {
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(4))

    val mongoAdapter = MongoAdapter(System.getenv("db_url"), System.getenv("db_data"), "userData")

    ISocketClient.get().run {
        capabilities(
            LoadUserPackage::class,
            BulkSaveUserPackage::class,
            SaveUserPackage::class,
            TopPackage::class
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
            mongoAdapter.save(pckg.userInfo)
            println("Received SaveUserPackage from ${realmId.realmName} for ${pckg.uuid}")

        }
        listen<BulkSaveUserPackage> { realmId, pckg ->
            mongoAdapter.save(pckg.packages.map { it.userInfo })
            println("Received BulkSaveUserPackage from ${realmId.realmName}")
        }
        listen<TopPackage> { realmId, pckg ->
            CoroutineScope(Dispatchers.IO).launch {
                val top = mongoAdapter.getTop(pckg.topType, pckg.limit)
                pckg.entries = top
                forward(realmId, pckg)
                println("Top generated for ${realmId.realmName}")
            }
        }
    }
}
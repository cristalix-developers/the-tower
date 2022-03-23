package me.reidj.tower

import dev.implario.bukkit.platform.Platforms
import dev.implario.bukkit.world.Label
import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.NoopGameNode
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import net.minecraft.server.v1_12_R1.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService

lateinit var app: App

class App : JavaPlugin(), Listener {

    val map = WorldMeta(MapLoader.load("func", "tower"))
    val client = CoordinatorClient(NoopGameNode())
    val spawn: Label = map.getLabel("spawn").apply {
        x += 0.5
        z += 0.5
        yaw = -90f
    }
    val tower = map.getLabel("tower").apply {
        x += 0.5
        z += 0.5
    }
    private val generators: MutableList<Label> = map.getLabels("mob")

    override fun onEnable() {
        app = this

        CoreApi.get().apply {
            registerService(ITransferService::class.java, TransferService(this.socketClient))
            registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
        }

        Platforms.set(PlatformDarkPaper())

        Anime.include(Kit.STANDARD, Kit.EXPERIMENTAL, Kit.DIALOG, Kit.NPC)
        ModLoader.loadAll("mods")

        IRealmService.get().currentRealmInfo.apply {
            status = RealmStatus.WAITING_FOR_PLAYERS
            isLobbyServer = true
            readableName = "Просто снос башки"
            groupName = "Секрет"
            servicedServers = arrayOf("SEC")
        }

        Bukkit.getPluginManager().apply {
            registerEvents(this@App, this@App)
        }
    }

    @EventHandler
    fun PlayerJoinEvent.handle() = player.apply {
        teleport(spawn)
        MinecraftServer.SERVER.postToNextTick {
            // Отправляем наш мод
            ModLoader.send("tower-mod-bundle.jar", player)

            // Отправляем точку башни
            ModTransfer(tower.x, tower.y, tower.z).send("tower:init", player)

            // Отправляем точки со спавнерами
            generators.forEach {
                ModTransfer()
                    .double(it.x)
                    .double(it.y)
                    .double(it.z)
                    .send("mobs:init", player)
            }
        }
    }
}
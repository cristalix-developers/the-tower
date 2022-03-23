package me.reidj.tower

import clepto.bukkit.B
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
import me.reidj.tower.listener.InteractEvent
import me.reidj.tower.listener.JoinEvent
import me.reidj.tower.listener.UnusedEvent
import me.reidj.tower.user.Stat
import me.reidj.tower.user.User
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import ru.kdev.simulatorapi.Simulator

const val HUB = "HUB-2"

lateinit var app: App

class App : JavaPlugin() {

    lateinit var simulator: Simulator<App>

    val map = WorldMeta(MapLoader.load("func", "tower"))
    val client = CoordinatorClient(NoopGameNode())

    val spawn: Label = map.getLabel("spawn").apply { yaw = -90f }
    val gamePosition: Label = map.getLabel("start").apply { yaw = -90f }
    val tower = map.getLabel("tower").apply {
        x += 0.5
        z += 0.5
    }
    val generators: MutableList<Label> = map.getLabels("mob")

    override fun onEnable() {
        B.plugin = this
        app = this

        simulator = Simulator.createSimulator<App, User> {
            id = "tower"
            plugin = this@App

            userCreator {
                User(Stat(it, 0))
            }
        }

        CoreApi.get().apply {
            registerService(ITransferService::class.java, TransferService(this.socketClient))
            registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
        }

        Platforms.set(PlatformDarkPaper())

        Anime.include(Kit.STANDARD, Kit.EXPERIMENTAL, Kit.DIALOG, Kit.NPC)
        ModLoader.loadAll("mods")

        // Конфигурация реалма
        IRealmService.get().currentRealmInfo.apply {
            status = RealmStatus.WAITING_FOR_PLAYERS
            isLobbyServer = true
            readableName = "Просто снос башки"
            groupName = "Секрет"
            servicedServers = arrayOf("SEC")
        }

        // Регистрация обработчиков событий
        B.events(
            JoinEvent,
            UnusedEvent,
            InteractEvent
        )
    }
}
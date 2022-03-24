package me.reidj.tower

import clepto.bukkit.B
import dev.implario.bukkit.platform.Platforms
import dev.implario.bukkit.world.Label
import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.NoopGameNode
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import io.netty.buffer.Unpooled
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.listener.InteractEvent
import me.reidj.tower.listener.JoinEvent
import me.reidj.tower.listener.UnusedEvent
import me.reidj.tower.user.Stat
import me.reidj.tower.user.User
import me.reidj.tower.wave.WaveManager
import org.bukkit.Bukkit
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
import java.util.*

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

        WaveManager.runTaskTimer(this@App, 0, 1)

        Bukkit.getMessenger().registerIncomingPluginChannel(app, "tower:mobhit") { _, player, bytes ->
            val user = simulator.getUser<User>(player.uniqueId)
            user?.wave!!.aliveMobs.filter {
                it.uuid == UUID.fromString(
                    Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8)
                )
            }.forEach {
                if (it.hp > 0) {
                    it.hp--
                } else {
                    user.wave!!.aliveMobs.remove(it)
                    ModTransfer().string(it.uuid.toString()).send("tower:mobkill", player)
                }
            }
        }
    }
}
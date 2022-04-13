package me.reidj.tower

import clepto.bukkit.B
import dev.implario.bukkit.platform.Platforms
import dev.implario.bukkit.world.Label
import dev.implario.games5e.node.CoordinatorClient
import dev.implario.games5e.node.NoopGameNode
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import implario.humanize.Humanize
import io.netty.buffer.Unpooled
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.protocol.EndStatus
import me.func.protocol.GlowColor
import me.reidj.tower.content.MainGui
import me.reidj.tower.listener.ConnectionHandler
import me.reidj.tower.listener.InteractEvent
import me.reidj.tower.listener.UnusedEvent
import me.reidj.tower.mob.Mob
import me.reidj.tower.upgrade.Upgrade
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.upgrade.UpgradeType.*
import me.reidj.tower.user.Tower
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import me.reidj.tower.wave.WaveManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import ru.kdev.simulatorapi.createSimulator
import ru.kdev.simulatorapi.listener.SessionListener
import java.util.*
import kotlin.math.sqrt

const val HUB = "HUB-2"

lateinit var app: App

class App : JavaPlugin() {

    private val map = WorldMeta(MapLoader.load("func", "tower"))
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

        createSimulator<App, User> {
            id = "tower"
            plugin = this@App

            levelFormula { ((sqrt(5.0) * sqrt((this * 80 + 5).toDouble()) + 5) / 20).toInt() }

            expFormula { this * this - this / 2 }

            userCreator { uuid ->
                User(
                    uuid,
                    0,
                    values().associateWith { Upgrade(it, 1) }.toMutableMap(),
                    Tower(null, 5.0, 5.0, UpgradeType.values().associateWith { Upgrade(it, 1) }.toMutableMap())
                )
            }
        }

        CoreApi.get().apply {
            registerService(ITransferService::class.java, TransferService(this.socketClient))
            registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
            registerService(IInventoryService::class.java, InventoryService())
        }

        Platforms.set(PlatformDarkPaper())

        Anime.include(Kit.STANDARD, Kit.NPC)
        ModLoader.loadAll("mods")

        // Конфигурация реалма
        IRealmService.get().currentRealmInfo.apply {
            status = RealmStatus.WAITING_FOR_PLAYERS
            isLobbyServer = true
            readableName = "Просто снос башки"
            groupName = "Секрет"
            servicedServers = arrayOf("SEC")
        }

        // Создание контента
        UpgradeInventory
        MainGui

        // Регистрация обработчиков событий
        B.events(
            ConnectionHandler,
            UnusedEvent,
            InteractEvent
        )

        B.regCommand({ player, args ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.giveMoney(args[0].toInt())
            null
        }, "money")

        B.regCommand({ player, args ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.giveTokens(args[0].toInt())
            null
        }, "tokens")


        WaveManager.runTaskTimer(this@App, 0, 1)

        Bukkit.getMessenger().registerIncomingPluginChannel(app, "tower:mobhit") { _, player, bytes ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                filterMobs(this, bytes).filter { wave!!.aliveMobs.contains(it) }.forEach {
                    it.hp -= session.upgrade[DAMAGE]!!.getValue().toInt()
                    if (it.hp <= 0) {
                        val token = session.upgrade[CASH_BONUS_KILL]!!.getValue().toInt()

                        giveTokens(token)

                        ModTransfer(
                            it.uuid.toString(), "§b+$token §f${
                                Humanize.plurals(
                                    "жетон",
                                    "жетона",
                                    "жетонов",
                                    token
                                )
                            }"
                        ).send("tower:mobkill", player)

                        wave!!.aliveMobs.remove(it)
                    }
                }
            }
        }
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "tower:hittower") { _, player, bytes ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                filterMobs(this, bytes).forEach { mob ->
                    val wavePassed = wave
                    val waveLevel = wavePassed!!.level
                    val reward = formula(waveLevel)

                    tower.health -= mob.damage - session.upgrade[PROTECTION]!!.getValue()
                    Glow.animate(player, .5, GlowColor.RED)

                    tower.updateHealth()

                    if (tower.health <= 0) {
                        if (maxWavePassed > waveLevel)
                            maxWavePassed = waveLevel
                        LobbyItems.initialActionsWithPlayer(player)
                        setFlying(player)

                        // Игра закончилась
                        ModTransfer(false).send("tower:update-state", player)

                        Anime.showEnding(player, EndStatus.LOSE, "Волн пройдено:", "$waveLevel")
                        wavePassed.aliveMobs.forEach { ModTransfer(it.uuid.toString()).send("tower:mobkill", player) }
                        wavePassed.aliveMobs.clear()
                        inGame = false
                        giveTokens(-tokens)
                        wave = null
                        if (reward == 0)
                            return@registerIncomingPluginChannel
                        Anime.cursorMessage(
                            player,
                            "§e+$reward §f${Humanize.plurals("монета", "монеты", "монет", reward)}"
                        )
                        giveMoney(reward)
                    }
                }
            }
        }
    }

    override fun onDisable() = SessionListener.simulator.disable()

    private fun filterMobs(user: User, bytes: ByteArray): Set<Mob> {
        if (user.wave == null)
            return emptySet()
        return user.wave?.let {
            it.aliveMobs.filter { mob ->
                mob.uuid == UUID.fromString(
                    Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8)
                )
            }
        }!!.toSet()
    }

    private fun formula(number: Int): Int = (number * number - number) / 4

    fun setFlying(player: Player) = player.apply { allowFlight = !isFlying }
}
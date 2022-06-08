package me.reidj.tower

import dev.implario.bukkit.platform.Platforms
import dev.implario.bukkit.world.Label
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
import me.func.mod.util.listener
import me.func.protocol.EndStatus
import me.func.protocol.GlowColor
import me.reidj.tower.command.AdminCommands
import me.reidj.tower.command.PlayerCommands
import me.reidj.tower.content.MainGui
import me.reidj.tower.data.WipeDate
import me.reidj.tower.listener.ConnectionHandler
import me.reidj.tower.listener.InteractEvent
import me.reidj.tower.listener.UnusedEvent
import me.reidj.tower.mob.Mob
import me.reidj.tower.tournament.RatingType
import me.reidj.tower.tournament.Tournament
import me.reidj.tower.upgrade.SwordType
import me.reidj.tower.upgrade.Upgrade
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.upgrade.UpgradeType.*
import me.reidj.tower.user.Tower
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import me.reidj.tower.wave.WaveManager
import org.bukkit.Bukkit
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

    val map = WorldMeta(MapLoader.load("func", "tower"))
    val spawn: Label = map.getLabel("spawn").apply { yaw = -90f }
    val wipeDate = WipeDate(GregorianCalendar(2022, Calendar.JUNE, 1)).calendar

    override fun onEnable() {
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
                    values().filter { it.isUserUpgrade }.associateWith { Upgrade(it, 1) }.toMutableMap(),
                    SwordType.NONE,
                    Tower(
                        null,
                        5.0,
                        5.0,
                        UpgradeType.values().filter { !it.isUserUpgrade }.associateWith { Upgrade(it, 1) }
                            .toMutableMap()
                    ),
                    0,
                    0,
                    0,
                    Tournament(RatingType.NONE, mutableListOf())
                )
            }
        }

        CoreApi.get().apply {
            registerService(ITransferService::class.java, TransferService(this.socketClient))
            registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
            registerService(IInventoryService::class.java, InventoryService())
        }

        Anime.include(Kit.EXPERIMENTAL, Kit.STANDARD, Kit.DEBUG)

        Platforms.set(PlatformDarkPaper())

        ModLoader.loadAll("mods")

        // Конфигурация реалма
        IRealmService.get().currentRealmInfo.apply {
            status = RealmStatus.WAITING_FOR_PLAYERS
            isLobbyServer = true
            readableName = "Просто снос башки"
            groupName = "Секрет"
        }

        // Создание контента
        UpgradeInventory
        MainGui

        // Регистрация команд
        PlayerCommands

        // Регистрация админ команд
        AdminCommands

        // Регистрация обработчиков событий
        listener(ConnectionHandler, UnusedEvent, InteractEvent)

        // Обработка каждого тика
        TimerHandler(listOf(WaveManager)).runTaskTimer(this, 0, 1)

        // Если моб есть в списке, то отнимаем его хп
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "mob:hit") { _, player, bytes ->
            // Нужно для проверки кто нанёс урон, башня или игрок
            val pair = Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8).split(":")
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                findMob(this, pair[0].encodeToByteArray())?.let { mob ->
                    mob.hp -= if (pair[1].toBoolean()) sword.damage else session!!.upgrade[DAMAGE]!!.getValue()

                    if (mob.hp <= 0) {
                        val token = upgradeTypes[CASH_BONUS_KILL]!!.getValue().toInt()

                        giveTokens(token)

                        ModTransfer(
                            mob.uuid.toString(), "§b+$token §f${
                                Humanize.plurals(
                                    "жетон",
                                    "жетона",
                                    "жетонов",
                                    token
                                )
                            }"
                        ).send("mob:kill", player)

                        wave!!.aliveMobs.remove(mob)
                    }
                }
            }
        }
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "tower:hittower") { _, player, bytes ->
            // Если моб есть в списке, то отнимаем хп у башни
            val pair = Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8).split(":")
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                findMob(this, pair[0].encodeToByteArray())?.let { mob ->
                    val waveLevel = wave!!.level
                    val reward = formula(waveLevel)

                    tower.health -= mob.damage - session!!.upgrade[PROTECTION]!!.getValue()
                    Glow.animate(player, .5, GlowColor.RED)

                    tower.updateHealth()

                    // Провожу действия с игроком если он проигрывает
                    if (tower.health <= 0) {
                        if (maxWavePassed > waveLevel)
                            maxWavePassed = waveLevel
                        if (tournament != null)
                            tournament!!.end(this)

                        LobbyItems.initialActionsWithPlayer(player)
                        player.flying(false)
                        showToAll()

                        // Игра закончилась
                        ModTransfer(false).send("tower:update-state", player)

                        Anime.showEnding(player, EndStatus.LOSE, "Волн пройдено:", "$waveLevel")
                        wave!!.aliveMobs.clear(player)
                        inGame = false
                        session = null
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

        Runtime.getRuntime().addShutdownHook(Thread {
            SessionListener.simulator.disable()
        })
    }

    override fun onDisable() = SessionListener.simulator.disable()

    private fun findMob(user: User, bytes: ByteArray): Mob? {
        if (user.wave == null)
            return null
        return user.wave?.let {
            it.aliveMobs.find { mob ->
                mob.uuid == UUID.fromString(
                    Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8)
                )
            }
        }
    }

    private fun formula(number: Int): Int = (number * number - number) / 4
}
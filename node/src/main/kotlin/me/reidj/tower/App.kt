package me.reidj.tower

import dev.implario.bukkit.platform.Platforms
import dev.implario.bukkit.world.Label
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import dev.xdark.paper.MaterialRegistry
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
import net.minecraft.server.v1_12_R1.BlockStone
import net.minecraft.server.v1_12_R1.Blocks
import net.minecraft.server.v1_12_R1.IBlockData
import net.minecraft.server.v1_12_R1.Item
import org.bukkit.Bukkit
import org.bukkit.Material
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

    override fun onEnable() {
        app = this

        val material = Material("block", 274, 834)
        val block = object : BlockStone() {
            override fun isFullBlock(blockData: IBlockData): Boolean {
                return true
            }

            override fun isTranslucent(iblockdata: IBlockData?): Boolean {
                return false
            }

            override fun isFullCube(iblockdata: IBlockData?): Boolean {
                return true
            }

            override fun isOpaqueCube(iblockdata: IBlockData): Boolean {
                return true
            }

            override fun isTopSolid(blockData: IBlockData): Boolean {
                return true
            }
        }
        block.setHardness(1.5F)
        MaterialRegistry.register(material)
        Item.register(block)
        Blocks.a(
            274,
            "block",
            block
        )

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

        Anime.include(Kit.NPC)
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
        app.listener(ConnectionHandler, UnusedEvent, InteractEvent)

        command("money") { player, args ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.giveMoney(args[0].toInt())
        }

        command("tokens") { player, args ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.giveTokens(args[0].toInt())
        }


        WaveManager.runTaskTimer(this@App, 0, 1)

        Bukkit.getMessenger().registerIncomingPluginChannel(app, "mob:hit") { _, player, bytes ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                findMob(this, bytes)?.let { mob ->
                    mob.hp -= session!!.upgrade[DAMAGE]!!.getValue().toInt()
                    if (mob.hp <= 0) {
                        val token = session!!.upgrade[CASH_BONUS_KILL]!!.getValue().toInt()

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
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                findMob(this, bytes)?.let { mob ->
                    val wavePassed = wave
                    val waveLevel = wavePassed!!.level
                    val reward = formula(waveLevel)

                    tower.health -= mob.damage - session!!.upgrade[PROTECTION]!!.getValue()
                    Glow.animate(player, .5, GlowColor.RED)

                    tower.updateHealth()

                    if (tower.health <= 0) {
                        if (maxWavePassed > waveLevel)
                            maxWavePassed = waveLevel
                        LobbyItems.initialActionsWithPlayer(player)
                        player.flying(false)

                        // Игра закончилась
                        ModTransfer(false).send("tower:update-state", player)

                        Anime.showEnding(player, EndStatus.LOSE, "Волн пройдено:", "$waveLevel")
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
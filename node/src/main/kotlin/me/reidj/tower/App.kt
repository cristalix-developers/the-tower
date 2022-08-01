package me.reidj.tower

import dev.implario.bukkit.platform.Platforms
import dev.implario.bukkit.world.Label
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import implario.humanize.Humanize
import io.netty.buffer.Unpooled
import kotlinx.coroutines.runBlocking
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.mod.util.listener
import me.func.protocol.EndStatus
import me.func.protocol.GlowColor
import me.reidj.tower.command.AdminCommands
import me.reidj.tower.command.PlayerCommands
import me.reidj.tower.content.MainGui
import me.reidj.tower.laboratory.LaboratoryManager
import me.reidj.tower.laboratory.ResearchType
import me.reidj.tower.listener.ConnectionHandler
import me.reidj.tower.listener.InteractEvent
import me.reidj.tower.listener.UnusedEvent
import me.reidj.tower.mob.Mob
import me.reidj.tower.npc.NpcManager
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.upgrade.UpgradeType.PROTECTION
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import me.reidj.tower.wave.WaveManager
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
import ru.cristalix.core.permissions.PermissionService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import ru.cristalix.simulatorapi.Simulator
import ru.cristalix.simulatorapi.createSimulator
import ru.cristalix.simulatorapi.top.TopManager
import java.util.*
import kotlin.math.sqrt

const val HUB = "HUB-2"

lateinit var app: App

class App : JavaPlugin() {

    val map = WorldMeta(MapLoader.load("func", "tower"))
    val spawn: Label = map.getLabel("spawn").apply { yaw = 0f }

    lateinit var simulator: Simulator<User>

    override fun onEnable() {
        app = this

        // Регистрация обработчиков событий
        listener(ConnectionHandler, UnusedEvent, InteractEvent)

        simulator = createSimulator {
            id = "tower"

            levelFormula { ((sqrt(5.0) * sqrt((this * 80 + 5).toDouble()) + 5) / 20).toInt() }

            expFormula { this * this - this / 2 }

            userCreator { uuid -> User(uuid) }
        }

        map.getLabel("top").run {
            y += 4.5
            yaw = 90f
            TopManager.create("rebirth", "Топ по Ребитху", "Ребитх", this)
        }

        simulator.topEnable()

        CoreApi.get().run {
            PermissionService(socketClient).enable()
            registerService(ITransferService::class.java, TransferService(socketClient))
            registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
            registerService(IInventoryService::class.java, InventoryService())
        }

        Anime.include(Kit.NPC, Kit.DIALOG, Kit.EXPERIMENTAL, Kit.STANDARD)

        Platforms.set(PlatformDarkPaper())

        ModLoader.loadAll("mods")

        // Конфигурация реалма
        IRealmService.get().currentRealmInfo.run {
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

        // Обработка каждого тика
        TimerHandler(listOf(WaveManager, NpcManager, LaboratoryManager)).runTaskTimer(this, 0, 1)

        // Если моб есть в списке, то отнимаем его хп
        Anime.createReader("mob:hit") { player, buffer ->
            // Нужно для проверки кто нанёс урон, башня или игрок
            val pair = buffer.toString(Charsets.UTF_8).split(":")
            coroutine {
                withUser(player) {
                    val session = session ?: return@withUser
                    findMob(this, pair[0].encodeToByteArray())?.let { mob ->
                        val damage =
                            session.upgrade[UpgradeType.DAMAGE]!!.getValue() + researchTypes[ResearchType.DAMAGE]!!.getValue()
                        if (pair[1].toBoolean()) {
                            val swordDamage = sword.damage
                            mob.hp -= swordDamage
                            Anime.killboardMessage(player, "Вы нанесли §c§l$swordDamage §fурона")
                        } else if (Math.random() > tower.upgrades[UpgradeType.CRITICAL_STRIKE_CHANCE]!!.getValue()) {
                            val criticalDamage =
                                damage + tower.upgrades[UpgradeType.CRITICAL_HIT_RATIO]!!.getValue() + researchTypes[ResearchType.CRITICAL_HIT]!!.getValue()
                            mob.hp -= criticalDamage
                            Anime.killboardMessage(player, "Башня нанесла §c§l$criticalDamage §fкритического урона")
                        } else {
                            mob.hp -= damage
                            Anime.killboardMessage(player, "Башня нанесла §c§l$damage §fурона")
                        }

                        if (mob.hp <= 0) {
                            val token = upgradeTypes[UpgradeType.CASH_BONUS_KILL]!!.getValue()
                                .toInt() + researchTypes[ResearchType.CASH_BONUS_KILL]!!.getValue().toInt()

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
        }

        Anime.createReader("tower:hittower") { player, buffer ->
            // Если моб есть в списке, то отнимаем хп у башни
            val pair = buffer.toString(Charsets.UTF_8).split(":")
            coroutine {
                withUser(player) {
                    findMob(this, pair[0].encodeToByteArray())?.let { mob ->
                        val waveLevel = wave!!.level
                        val reward = (waveLevel * waveLevel - waveLevel) / 4
                        val damage = mob.damage - session!!.upgrade[PROTECTION]!!.getValue()
                        after {
                            tower.health -= damage
                            Glow.animate(player, .5, GlowColor.RED)
                            Anime.killboardMessage(player, "Вам нанесли §c§l$damage урона")

                            tower.updateHealth()

                            // Провожу действия с игроком если он проигрывает
                            if (tower.health <= 0) {
                                if (maxWavePassed > waveLevel)
                                    maxWavePassed = waveLevel

                                if (isTournament) {
                                    TournamentManager.end(this)
                                    isTournament = false
                                }

                                LobbyItems.initialActionsWithPlayer(player)
                                player.flying(false)
                                coroutine { showToAll() }

                                // Игра закончилась
                                ModTransfer(false).send("tower:update-state", player)

                                Anime.showEnding(player, EndStatus.LOSE, "Волн пройдено:", "$waveLevel")
                                wave?.aliveMobs?.clear(player)
                                inGame = false
                                session = null
                                giveTokens(-tokens)
                                giveExperience(waveLevel * 3)
                                wave = null


                                if (reward == 0)
                                    return@after

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
        }
    }

    override fun onDisable() {
        runBlocking { simulator.disable() }
        Thread.sleep(1000)
    }

    private fun findMob(user: User, bytes: ByteArray): Mob? {
        if (user.wave == null)
            return null
        return user.wave!!.aliveMobs.find { mob ->
            mob.uuid == UUID.fromString(
                Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8)
            )
        }
    }
}
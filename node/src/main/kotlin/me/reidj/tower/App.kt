package me.reidj.tower

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import io.netty.buffer.Unpooled
import kotlinx.coroutines.runBlocking
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.util.listener
import me.func.sound.Category
import me.func.sound.Music
import me.reidj.tower.arena.ArenaManager
import me.reidj.tower.clock.GameTimer
import me.reidj.tower.clock.detail.DropItem
import me.reidj.tower.clock.detail.TopManager
import me.reidj.tower.command.AdminCommands
import me.reidj.tower.command.PlayerCommands
import me.reidj.tower.content.MainGui
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.ResearchType
import me.reidj.tower.donate.DonateMenu
import me.reidj.tower.game.wave.WaveManager
import me.reidj.tower.game.wave.mob.Mob
import me.reidj.tower.laboratory.LaboratoryManager
import me.reidj.tower.listener.InteractEvent
import me.reidj.tower.listener.PlayerMoveEvent
import me.reidj.tower.listener.UnusedEvent
import me.reidj.tower.npc.NpcManager
import me.reidj.tower.sword.SwordType
import me.reidj.tower.tournament.TournamentManager
import me.reidj.tower.upgrade.UpgradeMenu
import me.reidj.tower.user.PlayerDataManager
import me.reidj.tower.user.User
import me.reidj.tower.util.Formatter
import me.reidj.tower.util.MapLoader
import me.reidj.tower.util.plural
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import java.util.*
import kotlin.math.max

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

lateinit var app: App

val clientSocket: ISocketClient = ISocketClient.get()

class App : JavaPlugin() {

    lateinit var playerDataManager: PlayerDataManager
    lateinit var worldMeta: WorldMeta

    override fun onEnable() {
        app = this
        B.plugin = this

        Platforms.set(PlatformDarkPaper())

        Cristalix.client()

        CoreApi.get().run {
            registerService(ITransferService::class.java, TransferService(socketClient))
        }

        Anime.include(Kit.NPC, Kit.DIALOG, Kit.EXPERIMENTAL, Kit.STANDARD)

        ModLoader.loadAll("mods")

        IRealmService.get().currentRealmInfo.run {
            status = RealmStatus.WAITING_FOR_PLAYERS
            isLobbyServer = true
            readableName = "Просто снос башки"
            groupName = "Секрет"
        }

        Music.block(Category.VOICE).block(Category.PLAYERS)

        worldMeta = MapLoader().load("tower")!!

        ArenaManager()

        MainGui()
        UpgradeMenu()
        DonateMenu()

        DropItem()

        PlayerCommands()
        AdminCommands()

        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(
                this,
                GameTimer(listOf(WaveManager(), TopManager(), NpcManager(), LaboratoryManager(), TournamentManager())),
                0,
                1
            )

        playerDataManager = PlayerDataManager()

        listener(playerDataManager, InteractEvent(), UnusedEvent(), PlayerMoveEvent())

        Anime.createReader("mob:hit") { player, buffer ->
            // Нужно для проверки кто нанёс урон, башня или игрок
            val pair = buffer.toString(Charsets.UTF_8).split(":")
            (app.getUser(player) ?: return@createReader).run {
                val session = session ?: return@createReader
                app.findMob(this, pair[0].encodeToByteArray())?.let { mob ->
                    val damage =
                        session.towerImprovement[ImprovementType.DAMAGE]!!.getValue() + stat.researchType[ResearchType.DAMAGE]!!.getValue()
                    val damageFormat = damage.plural("урон", "урона", "урона")
                    val swordDamage = SwordType.valueOf(stat.sword).damage
                    val criticalDamage =
                        damage + tower!!.upgrades[ImprovementType.CRITICAL_HIT_RATIO]!!.getValue() + stat.researchType[ResearchType.CRITICAL_HIT]!!.getValue()
                    val isCriticalDamage =
                        Math.random() > tower!!.upgrades[ImprovementType.CRITICAL_STRIKE_CHANCE]!!.getValue()

                    mob.hitMob(
                        this,
                        if (pair[1].toBoolean()) swordDamage else if (isCriticalDamage) criticalDamage else damage,
                        if (pair[1].toBoolean()) "Вы нанесли §c§l${Formatter.toFormat(swordDamage)} §f$damageFormat" else if (isCriticalDamage) "Башня нанесла §c§l${
                            Formatter.toFormat(
                                criticalDamage
                            )
                        } §fкритического $damageFormat" else "Башня нанесла §c§l${Formatter.toFormat(damage)} §f$damageFormat"
                    )
                }
            }
        }

        Anime.createReader("tower:hittower") { player, buffer ->
            val user = app.getUser(player) ?: return@createReader
            val session = user.session ?: return@createReader
            val tower = user.tower ?: return@createReader
            // Если моб есть в списке, то отнимаем хп у башни
            val pair = buffer.toString(Charsets.UTF_8).split(":")
            app.findMob(user, pair[0].encodeToByteArray())?.let { mob ->
                val damage =
                    max(
                        1.0,
                        mob.damage - session.towerImprovement[ImprovementType.PROTECTION]!!.getValue() - user.stat.researchType[ResearchType.PROTECTION]!!.getValue()
                    )
                tower.hit(user, damage)
            }
        }
    }

    override fun onDisable() {
        runBlocking { clientSocket.write(playerDataManager.bulkSave(true)) }
        Thread.sleep(1000)
    }

    fun getNMSWorld(): WorldServer = worldMeta.world.handle

    fun getUser(uuid: UUID) = playerDataManager.userMap[uuid]

    fun getUser(player: Player) = getUser(player.uniqueId)

    fun getHub(): RealmId = RealmId.of("HUB-2")

    private fun findMob(user: User, bytes: ByteArray): Mob? {
        if (user.wave == null)
            return null
        return user.wave!!.aliveMobs.find { mob ->
            mob.uuid == UUID.fromString(Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8))
        }
    }
}
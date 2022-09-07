package me.reidj.tower

import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import io.netty.buffer.Unpooled
import kotlinx.coroutines.runBlocking
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModLoader
import me.func.mod.util.listener
import me.reidj.tower.clock.GameTimer
import me.reidj.tower.command.PlayerCommands
import me.reidj.tower.content.MainGui
import me.reidj.tower.game.Default
import me.reidj.tower.game.Rating
import me.reidj.tower.game.wave.WaveManager
import me.reidj.tower.game.wave.mob.Mob
import me.reidj.tower.laboratory.LaboratoryManager
import me.reidj.tower.listener.InteractEvent
import me.reidj.tower.listener.UnusedEvent
import me.reidj.tower.npc.NpcManager
import me.reidj.tower.top.TopManager
import me.reidj.tower.upgrade.UpgradeMenu
import me.reidj.tower.user.PlayerDataManager
import me.reidj.tower.user.User
import me.reidj.tower.util.MapLoader
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

        Platforms.set(PlatformDarkPaper())

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

        worldMeta = MapLoader().load("tower")!!

        playerDataManager = PlayerDataManager()

        Default()
        Rating()

        MainGui()
        UpgradeMenu()

        PlayerCommands()

        listener(playerDataManager, InteractEvent(), UnusedEvent())

        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(
                this,
                GameTimer(listOf(WaveManager(), TopManager(), LaboratoryManager(), NpcManager())),
                0,
                1
            )
    }

    override fun onDisable() {
        runBlocking { clientSocket.write(playerDataManager.bulkSave(true)) }
        Thread.sleep(1000)
    }

    fun getUser(uuid: UUID) = playerDataManager.userMap[uuid]

    fun getUser(player: Player) = getUser(player.uniqueId)

    fun getHub(): RealmId = RealmId.of("HUB-2")

    fun findMob(user: User, bytes: ByteArray): Mob? {
        if (user.wave == null)
            return null
        return user.wave!!.aliveMobs.find { mob ->
            mob.uuid == UUID.fromString(Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8))
        }
    }
}
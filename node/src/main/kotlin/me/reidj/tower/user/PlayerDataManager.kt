package me.reidj.tower.user

import clepto.bukkit.world.Label
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import me.func.mod.Anime
import me.func.mod.conversation.ModLoader
import me.func.mod.ui.booster.Booster
import me.func.mod.ui.booster.Boosters
import me.func.mod.util.after
import me.func.protocol.ui.indicator.Indicators
import me.reidj.tower.app
import me.reidj.tower.booster.BoosterInfo
import me.reidj.tower.booster.BoosterType
import me.reidj.tower.clientSocket
import me.reidj.tower.content.DailyRewardType
import me.reidj.tower.game.Rating
import me.reidj.tower.protocol.BulkSaveUserPackage
import me.reidj.tower.protocol.LoadUserPackage
import me.reidj.tower.protocol.SaveUserPackage
import me.reidj.tower.rank.RankManager
import me.reidj.tower.util.Images
import me.reidj.tower.util.giveDefaultItems
import me.reidj.tower.util.transfer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.cristalix.core.formatting.Formatting
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class PlayerDataManager : Listener {

    val userMap = mutableMapOf<UUID, User>()
    val thanksMap = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    val spawn: Label = app.worldMeta.getLabel("spawn").apply { yaw = 0f }

    var globalBoosters = mutableListOf<BoosterInfo>()

    @EventHandler
    fun AsyncPlayerPreLoginEvent.handle() = registerIntent(app).apply {
        CoroutineScope(Dispatchers.IO).launch {
            val statPackage = clientSocket.writeAndAwaitResponse<LoadUserPackage>(LoadUserPackage(uniqueId)).await()
            var stat = statPackage.stat
            if (stat == null) stat = DefaultElements.createNewUser(uniqueId)
            userMap[uniqueId] = User(stat)
            completeIntent(app)
        }
    }

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = app.getUser(player)

        if (user == null) {
            player.sendMessage(Formatting.error("Нам не удалось прогрузить Вашу статистику."))
            after(10) { player.transfer() }
            return
        }

        val stat = user.stat

        user.player = player
        user.tower = Tower(player, user.health, stat.maxHealth, stat.towerImprovementType, stat.researchType)

        after(3) {
            player.inventory.clear()
            player.gameMode = GameMode.ADVENTURE
            player.performCommand("resourcepack")
            player.giveDefaultItems()

            Anime.hideIndicator(
                player,
                Indicators.EXP,
                Indicators.ARMOR,
                Indicators.HUNGER,
                Indicators.HEALTH,
                Indicators.VEHICLE,
                Indicators.AIR_BAR
            )
            Anime.loadTextures(player, *Images.values().map { it.path() }.toTypedArray())

            ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", player)

            user.giveExperience(0)
            user.giveMoney(0.0)
            user.giveGem(0)

            RankManager.createRank(user)
            RankManager.showAll(user)

            if (app.playerDataManager.globalBoosters.isNotEmpty()) {
                sendBoosters(player, *app.playerDataManager.globalBoosters.toTypedArray())
            } else if (user.stat.localBoosters.isNotEmpty()) {
                sendBoosters(player, *user.stat.localBoosters.toTypedArray())
            }

            val now = System.currentTimeMillis().toDouble()
            // Обнулить комбо сбора наград если прошло больше суток или комбо > 7
            if ((stat.rewardStreak > 0 && now - stat.lastEnter > 24 * 60 * 60 * 1000) || stat.rewardStreak > 6) {
                stat.rewardStreak = 0
            }
            if (now - stat.dailyClaimTimestamp > 14 * 60 * 60 * 1000) {
                stat.dailyClaimTimestamp = now
                Anime.openDailyRewardMenu(
                    player,
                    stat.rewardStreak,
                    *DailyRewardType.values().map { it.reward }.toTypedArray()
                )
                val dailyReward = DailyRewardType.values()[stat.rewardStreak]
                player.sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.reward.title))
                dailyReward.give(user)
                stat.rewardStreak++
            }
            stat.lastEnter = now
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        val uuid = player.uniqueId
        val user = userMap.remove(uuid) ?: return
        Rating.queueLeave(player)
        RankManager.remove(uuid)
        clientSocket.write(SaveUserPackage(uuid, user.stat))
    }

    fun calcMultiplier(uuid: UUID, type: BoosterType): Double {
        globalBoosters.removeIf {
            val title = it.type.title
            Bukkit.broadcastMessage(Formatting.fine("Глобальный §bбустер $title §fзакончился!"))
            Boosters.send(Bukkit.getPlayer(uuid), Booster(it.uuid, false, "Бустер $title", it.multiplier))
            thanksMap.remove(it.uuid)
            it.hadExpire()
        }
        return (userMap[uuid] ?: return 1.0).calcMultiplier(type) + globalBoosters
            .filter { it.type === type && it.until > System.currentTimeMillis() }
            .sumOf { it.multiplier - 1.0 }
    }

    fun calcGlobalMultiplier(type: BoosterType) = 1f + globalBoosters
        .filter { it.type == type && it.until > System.currentTimeMillis() }.sumOf { it.multiplier - 1.0 }

    fun bulkSave(remove: Boolean): BulkSaveUserPackage? = BulkSaveUserPackage(Bukkit.getOnlinePlayers().map {
        val uuid = it.uniqueId
        val user = (if (remove) userMap.remove(uuid) else userMap[uuid]) ?: return null
        SaveUserPackage(uuid, user.stat)
    })

    fun sendBoosters(player: Player, vararg localBoosters: BoosterInfo) {
        localBoosters.forEach {
            Boosters.run {
                send(player, Booster(it.uuid, true, "Бустер ${it.type.title}", it.multiplier))
                mode(player, true)
            }
        }
    }
}
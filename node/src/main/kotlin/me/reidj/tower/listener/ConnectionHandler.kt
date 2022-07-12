package me.reidj.tower.listener

import me.func.mod.Alert
import me.func.mod.Alert.send
import me.func.mod.Anime
import me.func.mod.conversation.ModLoader
import me.func.mod.util.after
import me.func.protocol.Indicators
import me.func.protocol.Tricolor
import me.func.protocol.alert.NotificationData
import me.reidj.tower.content.DailyRewardType
import me.reidj.tower.npc.NpcManager
import me.reidj.tower.user.User
import me.reidj.tower.util.LobbyItems
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.cristalix.core.formatting.Formatting
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */

private const val NAMESPACE = "http://storage.c7x.ru/reidj/"

object ConnectionHandler : Listener {

    init {
        Alert.put(
            "resourcepack",
            NotificationData(
                null,
                "notify",
                "Рекомендуем установить ресурспак",
                0x2a66bd,
                0x183968,
                30000,
                listOf(
                    Alert.button(
                        "Установить",
                        "/resourcepack",
                        Tricolor(0, 180, 0)
                    )
                ),
                null
            )
        )
    }

    @EventHandler
    fun PlayerJoinEvent.handle() = player.apply {
        val user = SessionListener.simulator.getUser<User>(uniqueId)

        user?.player = this
        gameMode = GameMode.ADVENTURE
        LobbyItems.initialActionsWithPlayer(this)

        NpcManager.createNpcWithPlayerSkin(uniqueId)

        // Отправляем наш мод
        after(5) {
            ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", this)
            Anime.hideIndicator(
                this,
                Indicators.EXP,
                Indicators.ARMOR,
                Indicators.HUNGER,
                Indicators.HEALTH,
                Indicators.VEHICLE,
                Indicators.AIR_BAR
            )

            user?.giveMoney(-0)

            if (!user?.isAutoInstallResourcepack!!) Alert.find("resourcepack")
                .send(this) else performCommand("resourcepack")

            Anime.loadTextures(this, "${NAMESPACE}health_bar.png", "${NAMESPACE}energy.png", "${NAMESPACE}xp_bar.png")

            val now = System.currentTimeMillis().toDouble()
            // Обнулить комбо сбора наград если прошло больше суток или комбо > 7
            if ((user.day > 0 && now - user.lastEnter > 24 * 60 * 60 * 1000) || user.day > 6)
                user.day = 0
            if (now - user.dailyClaimTimestamp > 12 * 60 * 60 * 1000) {
                user.dailyClaimTimestamp = now
                Anime.openDailyRewardMenu(this, user.day, *DailyRewardType.values().map { it.reward }.toTypedArray())
                val dailyReward = DailyRewardType.values()[user.day]
                sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.reward.title))
                performCommand("lootboxsound")
                dailyReward.give.accept(user)
                user.day++
            }
            user.lastEnter = now
        }
    }
}
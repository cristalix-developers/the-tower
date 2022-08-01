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
import me.reidj.tower.coroutine
import me.reidj.tower.npc.NpcManager
import me.reidj.tower.util.GameUtil.queueLeave
import me.reidj.tower.util.Images
import me.reidj.tower.util.LobbyItems
import me.reidj.tower.withUser
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.cristalix.core.formatting.Formatting

/**
 * @project tower
 * @author Рейдж
 */

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
    fun PlayerJoinEvent.handle() {
        ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", player)

        player.inventory.clear()

        player.gameMode = GameMode.ADVENTURE
        LobbyItems.initialActionsWithPlayer(player)

        NpcManager.createNpcWithPlayerSkin(player.uniqueId)

        coroutine {
            withUser(player) {
                cachedPlayer = player
                after {
                    giveExperience(0)
                    giveMoney(-0)
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

                    if (!isAutoInstallResourcepack) Alert.find("resourcepack")
                        .send(player) else player.performCommand("resourcepack")

                    val now = System.currentTimeMillis().toDouble()
                    // Обнулить комбо сбора наград если прошло больше суток или комбо > 7
                    if ((day > 0 && now - lastEnter > 24 * 60 * 60 * 1000) || day > 6)
                        day = 0
                    if (now - dailyClaimTimestamp > 12 * 60 * 60 * 1000) {
                        dailyClaimTimestamp = now
                        Anime.openDailyRewardMenu(player, day, *DailyRewardType.values().map { it.reward }.toTypedArray())
                        val dailyReward = DailyRewardType.values()[day]
                        player.sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.reward.title))
                        player.performCommand("lootboxsound")
                        dailyReward.give.accept(this)
                        day++
                    }
                    lastEnter = now
                }
            }
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        queueLeave(player)
    }
}
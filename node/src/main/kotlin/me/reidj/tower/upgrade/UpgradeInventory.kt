package me.reidj.tower.upgrade

import me.func.mod.Anime
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.mod.util.nbt
import me.reidj.tower.barrier
import me.reidj.tower.item
import me.reidj.tower.text
import me.reidj.tower.user.User
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object UpgradeInventory {

    val workshop = item {}.nbt("other", "friend_add").nbt("click", "workshop")
        .text("§bМастерская\n\n§7Улучшайте навыки, чтобы проходить\n§7волны было ещё легче!")

    init {
        // Команда для открытия меню
        command("workshop") { player, _ ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                icon(this, if (!inGame) upgradeTypes else session!!.upgrade)
            }
        }
    }

    private val menu = selection {
        title = "Улучшения"
        hint = "Купить"
    }

    private fun icon(user: User, type: MutableMap<UpgradeType, Upgrade>) {
        val notInGame = !user.inGame
        menu.money = "Ваш баланс ${if (notInGame) user.money else user.tokens}"
        menu.rows = if (notInGame) 3 else 4
        menu.columns = if (notInGame) 1 else 3
        menu.storage = type.map { upgrades ->
            val key = upgrades.key
            val value = upgrades.value
            val level = value.level
            val cost = key.price + level
            button {
                item = key.item
                price = cost.toLong()
                title = "§3${value.level} LVL"
                description = key.lore
                onClick { player, _, _ ->
                    if (if (notInGame) user.money >= cost else user.tokens >= cost) {
                        if (notInGame) user.giveMoney(-cost) else user.giveTokens(-cost)
                        value.level++
                        player.performCommand("workshop")
                        user.tower.updateHealth()
                        if (notInGame) {
                            user.update(user)
                            user.sword.update(user)
                        } else {
                            user.session!!.update(
                                user,
                                UpgradeType.BULLET_DELAY,
                                UpgradeType.DAMAGE,
                                UpgradeType.HEALTH,
                                UpgradeType.PROTECTION,
                                UpgradeType.REGEN,
                                UpgradeType.RADIUS,
                            )
                        }
                    } else {
                        player.closeInventory()
                        Anime.itemTitle(player, barrier, "Ошибка", "Недостаточно средств", 2.0)
                    }
                }
            }
        }.toMutableList()
        menu.open(user.player!!)
    }
}
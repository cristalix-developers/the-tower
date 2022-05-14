package me.reidj.tower.upgrade

import me.func.mod.Anime
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.reidj.tower.item
import me.reidj.tower.nbt
import me.reidj.tower.text
import me.reidj.tower.user.User
import org.bukkit.Material.BARRIER
import org.bukkit.inventory.ItemStack
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object UpgradeInventory {

    val workshop = item {
        text("§bМастерская\n\n§7Улучшайте навыки, чтобы проходить\n§7волны было ещё легче!")
        nbt("other", "friend_add")
        nbt("click", "workshop")
    }

    init {
        // Команда для открытия меню
        command("workshop") { player, _ ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                icon(
                    this, if (!inGame) {
                        upgradeTypes
                        tower.upgrades
                    } else {
                        session!!.upgrade
                    }
                )
            }

        }
    }

    private fun icon(user: User, vararg upgradeTypes: MutableMap<UpgradeType, Upgrade>) {
        val notInGame = !user.inGame
        val menu = selection {
            title = "Улучшения башни"
            money = "Ваш баланс ${if (notInGame) user.money else user.tokens}"
            hint = "Купить"
            rows = 4
            columns = 3
            upgradeTypes.map { upgrades ->
                storage = upgrades.map { entry ->
                    val key = entry.key
                    val value = entry.value
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
                                user.player!!.performCommand("workshop")
                                user.tower.updateHealth()
                                if (notInGame)
                                    user.update(user)
                                else
                                    user.session!!.update(
                                        user,
                                        UpgradeType.BULLET_DELAY,
                                        UpgradeType.DAMAGE,
                                        UpgradeType.HEALTH,
                                        UpgradeType.PROTECTION,
                                        UpgradeType.REGEN,
                                        UpgradeType.RADIUS
                                    )
                            } else {
                                user.player!!.closeInventory()
                                Anime.itemTitle(player, ItemStack(BARRIER), "Ошибка", "Недостаточно средств", 2.0)
                            }
                        }
                    }
                }
            }
        }
        menu.open(user.player!!)
    }
}
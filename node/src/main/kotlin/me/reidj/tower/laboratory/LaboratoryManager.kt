package me.reidj.tower.laboratory

import implario.humanize.Humanize
import me.func.mod.Glow
import me.func.mod.selection.Confirmation
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.buyFailure
import me.reidj.tower.ticker.Ticked

/**
 * @project tower
 * @author Рейдж
 */
object LaboratoryManager : Ticked {

    private val menu = selection {
        title = "Лаборатория"
        columns = 3
        hint = "Изучить"
    }

    init {
        command("laboratory") { sender, _ ->
            val user = app.getUser(sender)
            menu.money = "Монет ${user?.money}"
            menu.open(sender)
            user?.let {
                menu.storage = it.researchTypes.map { entry ->
                    val key = entry.key
                    val cost = key.price
                    button {
                        title = "${key.title} §3${entry.value.level} LVL"
                        description = "§7${key.value} §f➠ §l${key.value + key.step}\nВремя улучшения ${key.duration} секунд"
                        item = key.item
                        price = cost.toLong()
                        onClick { click, _, _ ->
                            if (user.money >= cost) {
                                Confirmation(
                                    "Купить §a${key.title}",
                                    "§fза §b${key.price} ${
                                        Humanize.plurals(
                                            "монету",
                                            "монеты",
                                            "монет",
                                            key.price
                                        )
                                    }"
                                ) { player ->
                                    user.giveMoney(-cost)
                                    Glow.animate(player, 1.5, GlowColor.GREEN)
                                    // TODO покупка апгрейда
                                }
                            } else {
                                buyFailure(click)
                            }
                        }
                    }
                }.toMutableList()
            }
        }
    }

    override fun tick(vararg args: Int) {
        TODO("Not yet implemented")
    }
}
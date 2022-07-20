package me.reidj.tower.laboratory

import implario.humanize.Humanize
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.selection.Confirmation
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.buyFailure
import me.reidj.tower.ticker.Ticked
import me.reidj.tower.toFormat
import org.bukkit.Bukkit

/**
 * @project tower
 * @author Рейдж
 */
object LaboratoryManager : Ticked {

    private val menu = selection {
        title = "Лаборатория"
        columns = 2
    }

    init {
        command("laboratory") { sender, _ ->
            val user = app.getUser(sender)
            menu.money = "Монет ${user?.money}"
            user?.let {
                menu.storage = it.researchTypes.map { entry ->
                    val key = entry.key
                    val cost = key.price
                    button {
                        title = "${key.title} §3${entry.value.level} LVL"
                        description =
                                "${key.value} §f➠ §l${toFormat(key.value + key.step)}\nВремя улучшения ${key.duration} секунд"
                        item = key.item
                        price = cost.toLong()
                        hint(if (entry.value.whenBought == null) "Изучить" else "В процессе")
                        onClick { click, _, _ ->
                            if (entry.value.whenBought != null)
                                return@onClick
                            if (user.money >= cost) {
                                Confirmation(
                                        "Купить §a'${key.title}'",
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
                                    Glow.animate(player, 1.0, GlowColor.GREEN)
                                    Anime.title(click, "§dУспешно!")
                                    entry.value.whenBought =
                                            System.currentTimeMillis().toInt() / 1000 + entry.key.duration
                                }.open(click)
                            } else {
                                buyFailure(click)
                            }
                        }
                    }
                }.toMutableList()
            }
            menu.open(sender)
        }
    }

    override fun tick(vararg args: Int) {
        if (args[0] % 20 != 0)
            return
        Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }.forEach { user ->
            user.researchTypes.filter { it.value.whenBought != null }.forEach { (type, research) ->
                if (System.currentTimeMillis().toInt() / 1000 >= research.whenBought!!) {
                    user.player?.let { Anime.killboardMessage(it, "Завершено исследование: §a${type.title}") }
                    research.level++
                    research.whenBought = null
                }
            }
        }
    }
}
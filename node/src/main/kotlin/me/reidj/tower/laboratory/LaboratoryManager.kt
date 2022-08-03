package me.reidj.tower.laboratory

import implario.humanize.Humanize
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.selection.Button
import me.func.mod.selection.Confirmation
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.protocol.GlowColor
import me.reidj.tower.*
import me.reidj.tower.ticker.Ticked
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

    private lateinit var buttons: MutableList<Button>

    init {
        command("laboratory") { sender, _ ->
            coroutine {
                withUser(sender) {
                    menu.money = "Монет $money"
                    buttons = researchTypes.map { entry ->
                        val key = entry.key
                        val cost = key.price * entry.value.level - researchTypes[ResearchType.DISCOUNT]!!.getValue().toInt()
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
                                if (money >= cost) {
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
                                        giveMoney(-cost)
                                        Glow.animate(player, 1.0, GlowColor.GREEN)
                                        Anime.title(click, "§dУспешно!")
                                        entry.value.whenBought = System.currentTimeMillis().toInt() / 1000 + entry.key.duration - researchTypes[ResearchType.LABORATORY_SPEED]!!.getValue()
                                    }.open(click)
                                } else {
                                    buyFailure(click)
                                }
                            }
                        }
                    }.toMutableList()
                }
            }
            menu.storage = buttons
            menu.open(sender)
        }
    }

    override suspend fun tick(vararg args: Int) {
        if (args[0] % 20 != 0)
            return
        Bukkit.getOnlinePlayers().mapNotNull { getUser(it) }.forEach { user ->
            user.researchTypes.filter { it.value.whenBought != null }.forEach { (type, research) ->
                if (System.currentTimeMillis().toInt() / 1000 >= research.whenBought!!) {
                    user.cachedPlayer?.let {
                        Anime.killboardMessage(it, "Завершено исследование: §a${type.title}")
                    }
                    research.level++
                    research.whenBought = null
                } else {
                    user.cachedPlayer?.let { player ->
                        /*Banners.content(
                            player,
                            progressBanner,
                            "§bВ процессе\n§f${type.title} §3${
                                convertSecond(
                                    research.whenBought!! - System.currentTimeMillis().toInt() / 1000
                                )
                            }\n"
                        )*/
                    }
                }
            }
        }
    }
}
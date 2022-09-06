package me.reidj.tower.laboratory

import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.selection.Confirmation
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.data.ResearchType
import me.reidj.tower.util.Formatter.toFormat
import me.reidj.tower.util.error
import me.reidj.tower.util.plural
import org.bukkit.Bukkit

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class LaboratoryManager : ClockInject {

    private val menu = selection {
        title = "Лаборатория"
        rows = 3
        columns = 1
    }

    init {
        command("laboratory") { sender, _ ->
            (app.getUser(sender) ?: return@command).run {
                menu.money = "Монет ${toFormat(stat.money)}"
                menu.storage = stat.researchType.map { (key, value) ->
                    val cost = key.price * value.level - stat.researchType[ResearchType.DISCOUNT]!!.getValue()
                    button {
                        title = "${key.title} §3${value.level} LVL"
                        texture = key.texture
                        description =
                            "${toFormat(key.value + key.step)} §f➠ §l${key.value + key.step}\n" + "Время улучшения ${key.duration} секунд"
                        price = cost.toLong()
                        hint(if (value.whenBought == null) "Изучить" else "В процессе")
                        onClick { player, _, _ ->
                            if (value.whenBought != null)
                                return@onClick
                            if (stat.money >= cost) {
                                Confirmation(
                                    "Купить §a'${key.title}'",
                                    "§fза §b$cost ${cost.plural("монету", "монеты", "монет")}"
                                ) { accepter ->
                                    giveMoney(-cost)
                                    Glow.animate(player, 1.0, GlowColor.GREEN)
                                    Anime.title(accepter, "§dУспешно!")
                                    value.whenBought =
                                        System.currentTimeMillis()
                                            .toInt() / 1000 + key.duration - stat.researchType[ResearchType.LABORATORY_SPEED]!!.getValue()
                                }.open(player)
                            } else {
                                player.error("Недостаточно средств")
                            }
                        }
                    }
                }.toMutableList()
            }
            menu.open(sender)
        }
    }

    override fun run(tick: Int) {
        if (tick % 20 != 0)
            return
        Bukkit.getOnlinePlayers()
            .mapNotNull { app.getUser(it) }
            .forEach { user ->
                user.stat.researchType
                    .filter { it.value.whenBought != null }
                    .forEach { (key, value) ->
                        if (System.currentTimeMillis().toInt() / 1000 >= value.whenBought!!) {
                            Anime.killboardMessage(user.player, "Завершено исследование: §a${key.title}")
                            value.level++
                            value.whenBought = null
                        }
                    }
            }
    }
}
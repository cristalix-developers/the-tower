package me.reidj.tower.laboratory

import implario.humanize.Humanize
import me.func.mod.Anime
import me.func.mod.ui.Glow
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.status.MessageStatus
import me.reidj.tower.app
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.data.Category
import me.reidj.tower.data.ResearchType
import me.reidj.tower.util.Formatter.toFormat
import me.reidj.tower.util.PATH
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
        command("laboratory") { sender, args ->
            (app.getUser(sender) ?: return@command).run {
                menu.money = "Монет ${toFormat(stat.money)}"
                menu.storage =
                    stat.researchType.filter { it.key.category == Category.valueOf(args[0]) }.map { (key, value) ->
                        val cost = key.price * value.level - stat.researchType[ResearchType.DISCOUNT]!!.getValue()
                        val gem = 5 * value.level
                        button {
                            title = "${key.title} §3${value.level} LVL"
                            menu.vault = "${PATH}coin.png"
                            texture = key.texture
                            description =
                                if (value.whenBought != null) "Нажмите ПКМ, чтобы изучить прямо сейчас" else "${
                                    toFormat(value.getValue())
                                } §f➠ §l${toFormat(key.value + key.step * (value.level + 1))}\n" + "Время улучшения ${key.duration} секунд"
                            price = cost.toLong()
                            hint(if (value.whenBought == null) "Изучить" else "В процессе")
                            onLeftClick { player, _, _ ->
                                if (value.whenBought != null)
                                    return@onLeftClick
                                if (stat.money >= cost) {
                                    Confirmation(
                                        "Купить §a'${key.title}'",
                                        "§fза §b${toFormat(cost)} ${cost.plural("монету", "монеты", "монет")}"
                                    ) { accepter ->
                                        giveMoney(-cost)
                                        Glow.animate(player, 1.0, GlowColor.GREEN)
                                        Anime.title(accepter, "§dУспешно!")
                                        value.whenBought =
                                            System.currentTimeMillis()
                                                .toInt() / 1000 + key.duration - stat.researchType[ResearchType.LABORATORY_SPEED]!!.getValue()
                                    }.open(player)
                                } else {
                                    Anime.systemMessage(player, MessageStatus.ERROR, "Недостаточно средств")
                                }
                            }
                            onRightClick { player, _, _ ->
                                if (value.whenBought != null)
                                    return@onRightClick
                                if (stat.gem >= gem) {
                                    Confirmation(
                                        "Купить §a'Спешка'",
                                        "§fза §d$gem ${Humanize.plurals("Самоцвет", "Самоцвета", "Самоцветов", gem)}"
                                    ) { accepter ->
                                        giveGem(-gem)
                                        Glow.animate(player, 1.0, GlowColor.GREEN)
                                        Anime.title(accepter, "§dУспешно!")
                                        Anime.killboardMessage(player, "Завершено исследование: §a${key.title}")
                                        value.level++
                                        value.whenBought = null
                                    }.open(player)
                                } else {
                                    Anime.systemMessage(player, MessageStatus.ERROR, "Недостаточно средств")
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
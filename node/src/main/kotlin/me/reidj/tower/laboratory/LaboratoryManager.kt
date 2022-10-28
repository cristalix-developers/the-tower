package me.reidj.tower.laboratory

import implario.humanize.Humanize
import me.func.mod.Anime
import me.func.mod.ui.Glow
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.reidj.tower.app
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.data.Category
import me.reidj.tower.data.ResearchType
import me.reidj.tower.sound.SoundType
import me.reidj.tower.util.Formatter.toFormat
import me.reidj.tower.util.PATH
import me.reidj.tower.util.error
import me.reidj.tower.util.formatSecond
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
                                if (value.whenBought != 0L) "Нажмите ПКМ, чтобы закончить изучение" else "${
                                    toFormat(
                                        value.getValue()
                                    )
                                } §f➠ §l${toFormat(key.value + key.step * (value.level + 1))}\n" + "Время улучшения ${key.duration} секунд"
                            price = cost.toLong()
                            hint(if (value.whenBought == 0L) "Изучить" else "В процессе")
                            onLeftClick { player, _, _ ->
                                if (value.whenBought != 0L)
                                    return@onLeftClick
                                Confirmation(
                                    "Купить §a'${key.title}'",
                                    "§fза §b${toFormat(cost)} ${cost.plural("монету", "монеты", "монет")}"
                                ) { accepter ->
                                    if (!(app.getUser(accepter) ?: return@Confirmation).armLock()) {
                                        if (stat.money >= cost) {
                                            giveMoney(-cost)
                                            SoundType.BUY.send(player)
                                            Glow.animate(player, 1.0, GlowColor.GREEN)
                                            Anime.title(accepter, "§dУспешно!")
                                            app.playerDataManager.addProgress(this@run, key)
                                            value.whenBought = System.currentTimeMillis() / 1000
                                        } else {
                                            player.error("Недостаточно средств")
                                        }
                                    }
                                }.open(player)
                            }
                            onRightClick { player, _, _ ->
                                if (value.whenBought != 0L)
                                    return@onRightClick
                                Confirmation(
                                    "Купить §a'Спешка'",
                                    "§fза §d$gem ${Humanize.plurals("Самоцвет", "Самоцвета", "Самоцветов", gem)}"
                                ) { accepter ->
                                    if (stat.gem >= gem) {
                                        giveGem(-gem)
                                        Glow.animate(player, 1.0, GlowColor.GREEN)
                                        Anime.title(accepter, "§dУспешно!")
                                        Anime.killboardMessage(player, "Завершено исследование: §a${key.title}")
                                        value.level++
                                        value.whenBought = 0
                                    } else {
                                        player.error("Недостаточно средств")
                                    }
                                }.open(player)

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
            .forEach user@{ user ->
                user.stat.researchType
                    .forEach { (key, value) ->
                        val now = System.currentTimeMillis()
                        val progress = user.activeProgress[key] ?: return@forEach
                        val whenBought = value.whenBought * 1000
                        val end = whenBought + value.getFullDuration() * 1000
                        if (now >= end) {
                            Anime.killboardMessage(user.player, "Завершено исследование: §a${key.title}")
                            progress.delete(setOf(user.player))
                            user.activeProgress.remove(key)
                            app.playerDataManager.updateProgressOffset(user, *user.activeProgress.map { it.key }.toTypedArray())
                            value.whenBought = 0
                            value.level++
                        } else {
                            progress.progress = 1 - (now * 1.0 - whenBought) / (end - whenBought)
                            progress.text =
                                "${key.title} ${formatSecond(-((now - value.whenBought) - (end - value.whenBought)) / 1000)}"
                        }
                    }
            }
    }
}

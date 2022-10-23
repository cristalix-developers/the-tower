package me.reidj.tower.content

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.after
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.util.CategoryMenu
import me.reidj.tower.util.Formatter
import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class MainGui {

    private val menu = selection {
        title = "Tower Simulator"
        rows = 3
        columns = 1
        money = ""
        hint = ""
    }

    private val buttons = mutableListOf(
        button {
            texture = "${PATH}statistic.png"
            title = "§6Статистика"
            description = "§7Наведите, чтобы посмотреть"
        }, button {
            texture = "${PATH}workshop.png"
            title = "Мастерская"
            description = "§7Улучшайте навыки, чтобы проходить волны было ещё легче!"
            hint("Открыть")
            onClick { player, _, _ -> CategoryMenu.open("workshop", player, 1) }
        }, button {
            texture = "${PATH}laboratory.png"
            title = "Лаборатория"
            description = "§7Это место, где вы можете улучшить свои навыки."
            hint("Исследовать")
            onClick { player, _, _ -> CategoryMenu.open("laboratory", player, null) }
        }
    )

    init {
        command("menu") { player, _ ->
            (app.getUser(player) ?: return@command).run {
                after {
                    buttons[0].hover = """
                         §7Монеты: §3${Formatter.toFormat(stat.money)}
                         §7Самоцветы: §3${stat.gem}
                         §7Опыт: §3${Formatter.toFormat(requiredExperience())}
                         §7Волн пройдено: §3${stat.maximumWavePassed}
                         §7Ранг: §3${stat.rank.title}
                """.trimIndent()
                }
            }
            menu.storage = buttons
            menu.open(player)
        }
    }
}
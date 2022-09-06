package me.reidj.tower.content

import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.util.Formatter
import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class MainGui {

    private val menu = selection {
        title = "Tower Simulator"
        rows = 4
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
            onClick { player, _, _ -> player.performCommand("workshop") }
        }, button {
            texture = "${PATH}laboratory.png"
            title = "Лаборатория"
            description = "§7Это место, где вы можете улучшить свои навыки."
            hint("Исследовать")
            onClick { player, _, _ -> player.performCommand("laboratory") }
        }, button {
            texture = "${PATH}settings.png"
            title = "Ресурспак"
            hint("Переключить")
            onClick { player, _, button ->
                (app.getUser(player) ?: return@onClick).run {
                    stat.isAutoInstallResourcepack = !stat.isAutoInstallResourcepack
                    button.hint(if (stat.isAutoInstallResourcepack) "Не устанавливать" else "Устанавливать автоматически")
                }
            }
        }
    )

    init {
        command("menu") { player, _ ->
            (app.getUser(player) ?: return@command).stat.run {
                buttons[0].hover = """
                         §7Монет: §3${Formatter.toFormat(money)}
                         §7Волн пройдено: §3${maximumWavePassed}
                """.trimIndent()
            }
            menu.storage = buttons
            menu.open(player)
        }
    }
}
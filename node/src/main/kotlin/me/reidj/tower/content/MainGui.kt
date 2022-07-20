package me.reidj.tower.content

import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.mod.util.nbt
import me.reidj.tower.app
import me.reidj.tower.item
import me.reidj.tower.upgrade.UpgradeInventory

/**
 * @project tower
 * @author Рейдж
 */
object MainGui {

    private val resourcepack = item { }.nbt("other", "settings")
    private val statistic = item {}.nbt("other", "quest_week")

    private val menu = selection {
        title = "Tower Simulator"
        rows = 2
        columns = 1
        money = ""
        hint = ""
    }

    private val workshop = button {
        item = UpgradeInventory.workshop
        title = "Мастерская"
        description = "§7Улучшайте навыки, чтобы проходить волны было ещё легче!"
        hint("Открыть")
        onClick { player, _, _ -> player.performCommand("workshop") }
    }

    init {
        command("menu") { opened, _ ->
            val user = app.getUser(opened) ?: return@command
            menu.storage.clear()
            menu.storage.addAll(listOf(
                button {
                    item = statistic
                    title = "§f§l > §bСтатистика"
                    description = """
                            §7    Монет: §e${user.money}
                            §7    Волн пройдено: §b${user.maxWavePassed}
                        """.trimIndent()
                },
                workshop,
                button {
                    item = resourcepack
                    title =
                        if (user.isAutoInstallResourcepack) "Не устанавливать ресурспак" else "Устанавливать ресурспак автоматически"
                    hint("Переключить")
                }
            ))
            menu.open(opened)
        }
    }
}
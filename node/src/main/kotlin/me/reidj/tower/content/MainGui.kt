package me.reidj.tower.content

import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.mod.util.nbt
import me.reidj.tower.coroutine
import me.reidj.tower.item
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.withUser

/**
 * @project tower
 * @author Рейдж
 */
object MainGui {

    private val resourcePackIcon = item().nbt("other", "settings")
    private val statisticIcon = item().nbt("other", "quest_week")
    private val laboratoryIcon = item().nbt("skyblock", "glowing_redstone")

    private val menu = selection {
        title = "Tower Simulator"
        rows = 2
        columns = 1
        money = ""
        hint = ""
    }

    private val statistic = button {
        item = statisticIcon
        title = "§f§l > §bСтатистика"
    }

    private val workshop = button {
        item = UpgradeInventory.workshop
        title = "Мастерская"
        description = "§7Улучшайте навыки, чтобы проходить волны было ещё легче!"
        hint("Открыть")
        onClick { player, _, _ -> player.performCommand("workshop") }
    }

    private val resourcePack = button {
        item = resourcePackIcon
        title = "Ресурспак"
        onClick { player, _, button ->
            coroutine {
                withUser(player) {
                    isAutoInstallResourcepack = !isAutoInstallResourcepack
                    button.hint(if (isAutoInstallResourcepack) "Не устанавливать" else "Устанавливать автоматически")
                }
            }
        }
    }

    private val laboratory = button {
        item = laboratoryIcon
        title = "Лаборатория"
        description = "§7Это место, где вы можете улучшить свои навыки."
        hint("Исследовать")
        onClick { player, _, _ -> player.performCommand("laboratory") }
    }

    init {
        command("menu") { opened, _ ->
            coroutine {
                withUser(opened) {
                    statistic.description = """
                            §7    Монет: §e${money}
                            §7    Волн пройдено: §b${maxWavePassed}
                        """.trimIndent()
                    resourcePack.hint(if (isAutoInstallResourcepack) "Не устанавливать" else "Устанавливать автоматически")
                }
            }
            menu.storage = mutableListOf(statistic, workshop, laboratory, resourcePack)
            menu.open(opened)
        }
    }
}
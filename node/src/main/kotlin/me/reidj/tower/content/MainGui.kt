package me.reidj.tower.content

import dev.implario.bukkit.item.item
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.mod.util.nbt
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.user.User
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object MainGui {

    private val menu = selection {
        title = "Tower Simulator"
        rows = 2
        columns = 1
        money = ""
        hint = ""
        buttons(
            button {
                item = UpgradeInventory.workshop
                title = "Мастерская"
                description = "Улучшайте навыки, чтобы проходить\n§7волны было ещё легче!"
                onClick { player, _, _ -> player.performCommand("workshop") }
            }.hint("Открыть")
        )
    }

    init {
        command("menu") { opened, _ ->
            val user = SessionListener.simulator.getUser<User>(opened.uniqueId)!!
            if (menu.storage.size == 2)
                menu.storage.removeAt(1)
            menu.storage.add(button {
                item = item {}.nbt("other", "quest_week")
                title = "§f§l > §bСтатистика"
                description = """
                            §7    Монет: §e${user.money}
                            §7    Волн пройдено: §b${user.maxWavePassed}
                        """.trimIndent()
            })
            menu.open(opened)
        }
    }
}
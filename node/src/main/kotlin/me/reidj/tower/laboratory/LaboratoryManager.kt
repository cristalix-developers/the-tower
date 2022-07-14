package me.reidj.tower.laboratory

import me.func.mod.selection.selection
import me.func.mod.util.command
import me.reidj.tower.app

/**
 * @project tower
 * @author Рейдж
 */
object LaboratoryManager {

    private val menu = selection {
        title = "Лаборатория"
        columns = 3
        hint = "Изучить"
    }

    init {
        command("laboratory") { player, _ ->
            val user = app.getUser(player)
            menu.money = "Монет ${user?.money}"
            menu.open(player)
        }
    }
}
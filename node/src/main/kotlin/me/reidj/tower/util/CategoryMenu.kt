package me.reidj.tower.util

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.reidj.tower.data.Category
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
object CategoryMenu {

    private val menu = selection {
        title = "Категории"
        hint = "Перейти"
        rows = 3
        columns = 1
    }

    fun open(command: String, player: Player) {
        menu.storage = Category.values().map {
            button {
                title = it.title
                texture = it.texture
                onClick { player, _, _ -> player.performCommand("$command ${it.name}") }
            }
        }.toMutableList()
        menu.open(player)
    }
}
package me.reidj.tower.util

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.choicer
import me.reidj.tower.data.Category
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
object CategoryMenu {

    private val menu = choicer {
        title = "Категории"
        description = ""
    }

    fun open(command: String, player: Player, index: Int?) {
        val storage = Category.values().map {
            button {
                title = it.title
                texture = it.texture
                hint("Перейти")
                onClick { player, _, _ -> player.performCommand("$command ${it.name}") }
            }
        }.toMutableList()
        if (index != null) {
            storage.removeAt(index)
        }
        menu.storage = storage
        menu.open(player)
    }
}
package me.reidj.tower.game

import me.func.mod.util.command

/**
 * @project : tower
 * @author : Рейдж
 **/
object NormalGame : Game() {

    init {
        command("normal") { player, _ -> start(player) }
    }
}
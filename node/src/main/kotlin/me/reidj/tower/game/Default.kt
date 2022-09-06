package me.reidj.tower.game

import me.func.mod.util.command

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class Default : Game {

    init {
        command("default") { player, _ -> start(player) }
    }
}
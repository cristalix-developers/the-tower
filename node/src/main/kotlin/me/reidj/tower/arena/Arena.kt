package me.reidj.tower.arena

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.protocol.math.Position
import me.reidj.tower.user.User
import org.bukkit.Location

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Arena(
    val arenaNumber: Int,
    val arenaSpawn: Location,
    val cubeLocation: Location,
    val generators: Set<Location>,
    val gemLocations: Set<Location>
) {

    fun arenaChange(user: User) {
        val session = user.session!!
        Anime.alert(user.player, "Поздравляем!", "Вы прошли $arenaNumber уровень!")
        session.arena = ArenaManager.arenas[ArenaManager.arenas.indexOf(session.arena) + 1]
        ModTransfer(cubeLocation.x, cubeLocation.y, cubeLocation.z).send("tower:map-change", user.player)
        user.player.teleport(arenaSpawn)
        Anime.overlayText(user.player, Position.BOTTOM_RIGHT, "Уровень: §3${arenaNumber}")
    }
}

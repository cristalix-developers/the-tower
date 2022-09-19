package me.reidj.tower.arena

import org.bukkit.Location

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Arena(
    val arenaNumber: Int,
    val arenaSpawn: Location,
    val cubeLocation: Location,
    val generators: List<Location>,
    val gemLocations: List<Location>
)

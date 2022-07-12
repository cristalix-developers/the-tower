package me.reidj.tower.npc

import me.func.mod.Banners
import me.func.mod.Banners.location
import me.func.protocol.element.Banner
import me.reidj.tower.app
import org.bukkit.Location

enum class NpcType(
    val title: String,
    val location: Location,
    val command: String,
    val skin: String,
    val pitch: Float,
    var banner: Banner
) {
    NORMAL(
        "§bОбычная",
        app.map.getLabel("normal"),
        "normal",
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd",
        179f,
        createBanner(app.map.getLabel("normal"), 179.0, 0.62, 5.0, 1.0,false)
    ),
    RATING(
        "§bТурнир",
        app.map.getLabel("rating"),
        "tournamentDialog",
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd",
        179f,
        createBanner(app.map.getLabel("rating"), 179.0, 0.62, 5.0, 1.0,false)
    ),
    CHARACTER(
        "§6Ваш профиль Tower",
        app.map.getLabel("character"),
        "menu",
        "",
        160f,
        createBanner(app.map.getLabel("character"), 150.0, 0.0, 6.0, 0.5,true)
    ),
    ;
}

private fun createBanner(
    location: Location,
    yaw: Double,
    opacity: Double,
    y: Double,
    z: Double,
    watchingOnPlayer: Boolean
): Banner = Banners.new {
    height = 40
    weight = 73
    location(location.clone().add(0.5, y, z))
    motionSettings = hashMapOf(
        "yaw" to yaw,
        "pitch" to 0.0
    )
    this.opacity = opacity
    this.watchingOnPlayer = watchingOnPlayer
}
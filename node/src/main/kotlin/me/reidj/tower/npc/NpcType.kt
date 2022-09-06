package me.reidj.tower.npc

import me.func.mod.Banners
import me.func.mod.Banners.location
import me.func.protocol.element.Banner
import me.reidj.tower.app
import org.bukkit.Location

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class NpcType(
    val bannerTitle: String,
    val npcName: String,
    val command: String,
    var skinUrl: String,
    var skinDigest: String,
    val pitch: Float,
    var banner: Banner?
) {
    NORMAL(
        "§bОбычная",
        "",
        "normal",
        "https://webdata.c7x.dev/textures/skin/307264a1-2c69-11e8-b5ea-1cb72caa35fd",
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd",
        179f,
        createBanner(app.worldMeta.getLabel("normal"), 179.0, 0.62, 4.7, 1.0,false)
    ),
    RATING(
        "§bТурнир",
        "",
        "tournamentDialog",
        "https://webdata.c7x.dev/textures/skin/bf30a1df-85de-11e8-a6de-1cb72caa35fd",
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd",
        179f,
        createBanner(app.worldMeta.getLabel("rating"), 179.0, 0.62, 4.7, 1.0,false)
    ),
    CHARACTER(
        "§6Ваш профиль Tower",
        "§eНАЖМИТЕ ДЛЯ ПРОСМОТРА",
        "menu",
        "self",
        "self",
        160f,
        createBanner(app.worldMeta.getLabel("character"), 150.0, 0.0, 6.0, 0.5,true)
    ),
    GUIDE(
        "",
        "§aОБУЧЕНИЕ",
        "guide",
        "https://webdata.c7x.dev/textures/skin/d5c6967a-2fd9-11eb-acca-1cb72caa35fd",
        "d5c6967a-2fd9-11eb-acca-1cb72caa35fd",
        -156.5f,
        null
    ),
    LABORATORY(
        "",
        "§6РАБОТНИК ЛАБОРАТОРИИ",
        "laboratory",
        "https://webdata.c7x.dev/textures/skin/c87bbbbf-c7a4-11eb-acca-1cb72caa35fd",
        "c87bbbbf-c7a4-11eb-acca-1cb72caa35fd",
        131.5f,
        null
    )
    ;
}

private fun createBanner(
    location: Location,
    yaw: Double,
    opacity: Double,
    y: Double,
    z: Double,
    watchingOnPlayer: Boolean
) = Banners.new {
    height = 40
    weight = 93
    location(location.clone().add(0.5, y, z))
    motionSettings = hashMapOf(
        "yaw" to yaw,
        "pitch" to 0.0
    )
    this.opacity = opacity
    this.watchingOnPlayer = watchingOnPlayer
}
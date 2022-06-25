package me.reidj.tower.npc

import me.func.mod.Banners
import me.func.mod.Banners.location
import me.func.protocol.element.Banner
import me.reidj.tower.app
import me.reidj.tower.tournament.TournamentManager
import org.bukkit.Location

enum class NpcType(
    val title: String,
    val contentBuilder: () -> String,
    val location: Location,
    val command: String,
    val skin: String,
    val pitch: Float,
    var banner: Banner
) {
    NORMAL(
        "§bОбычная",
        { String.format(
            "%s\n${if (TournamentManager.isTournamentStarted()) "§e%d игроков\n§6До конца %dч" else "§6До начала %d дней"}",
            RATING.title,
            TournamentManager.getOnlinePlayers().filter { it.isTournament }.size,
            TournamentManager.hoursEndOfTournament(),
        ) },
        app.map.getLabel("normal"),
        "normal",
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd",
        179f,
        createBanner(app.map.getLabel("normal"))
    ),
    RATING(
        "§bТурнир",
        { "${NORMAL.title}\n§e${TournamentManager.getOnlinePlayers().size} игроков" },
        app.map.getLabel("rating"),
        "rating",
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd",
        179f,
        createBanner(app.map.getLabel("rating"))
    ),
    ;
}

private fun createBanner(location: Location): Banner = Banners.new {
    height = 40
    weight = 73
    location(location.clone().add(0.5, 5.0, 1.0))
    motionSettings = hashMapOf(
        "yaw" to 179.0,
        "pitch" to 0.0
    )
}
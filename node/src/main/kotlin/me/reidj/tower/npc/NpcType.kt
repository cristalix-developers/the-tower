package me.reidj.tower.npc

import me.func.mod.Banners
import me.func.mod.Banners.location
import me.func.protocol.element.Banner
import me.reidj.tower.app
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import org.bukkit.Location
import ru.kdev.simulatorapi.listener.SessionListener

enum class NpcType(
    val description: String,
    val location: Location,
    val command: String,
    val skin: String,
    val pitch: Float,
    var banner: Banner
) {
    NORMAL(
        String.format("§bОбычная\n§e%d игроков",
            Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId) }
                .filter { it.inGame }.size
        ),
        app.map.getLabel("normal"),
        "",
        "307264a1-2c69-11e8-b5ea-1cb72caa35fd",
        179f,
        createBanner(app.map.getLabel("normal"))
    ),
    RATING(
        String.format(
            "§bТурнир\n§e%d игроков",
            Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId) }
                .filter { it.inGame && it.isTournament }.size
        ),
        app.map.getLabel("rating"),
        "",
        "bf30a1df-85de-11e8-a6de-1cb72caa35fd",
        179f,
        createBanner(app.map.getLabel("rating"))
    ),
    ;
}

private fun createBanner(location: Location): Banner = Banners.new {
    height = 40
    weight = 66
    location(location.clone().add(0.5, 5.0, 1.0))
    motionSettings = hashMapOf(
        "yaw" to 179.0,
        "pitch" to 0.0
    )
}
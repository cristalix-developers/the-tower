package me.reidj.tower.clock.detail

import dev.implario.bukkit.item.item
import me.reidj.tower.app
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.tournament.TournamentManager
import org.bukkit.Material

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class DropItem : ClockInject {

    private val gem = item {
        type = Material.CLAY_BALL
        nbt("tower", "gem")
    }
    private val locations = app.worldMeta.getLabels("gem")

    override fun run(tick: Int) {
        if (tick % 120 == 0)
            TournamentManager.getOnlinePlayers()
                .forEach { _ -> app.worldMeta.world.dropItem(locations.random().apply { y += 2.0 }, gem) }
    }
}
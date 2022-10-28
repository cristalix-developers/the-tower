package me.reidj.tower.clock.detail

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import me.reidj.tower.app
import me.reidj.tower.sound.SoundType
import me.reidj.tower.tournament.TournamentManager
import org.bukkit.Material

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class DropItem {

    private val gem = item {
        type = Material.CLAY_BALL
        nbt("tower", "gem")
    }

    init {
        B.repeat(20 * 300) {
            TournamentManager.getOnlinePlayers()
                .forEach { user ->
                    app.worldMeta.world.dropItem(user.session?.arena?.gemLocations?.random(), gem)
                    SoundType.GEM_DROP.send(user.player)
                }
        }
    }
}
package me.reidj.tower.clock.detail

import clepto.bukkit.B
import me.reidj.tower.game.Gem
import me.reidj.tower.sound.SoundType
import me.reidj.tower.tournament.TournamentManager

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class DropItem {

    init {
        B.repeat(20 * 300) {
            TournamentManager.getOnlinePlayers().forEach { user ->
                val session = user.session
                Gem(session?.arena?.gemLocations?.random()!!).apply {
                    create(user.connection)
                    session.gems?.add(this)
                }
                SoundType.GEM_DROP.send(user.player)
            }
        }
    }
}
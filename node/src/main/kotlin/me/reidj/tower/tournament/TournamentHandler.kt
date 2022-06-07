package me.reidj.tower.tournament

import me.reidj.tower.app
import me.reidj.tower.isTournament
import me.reidj.tower.ticker.Ticked
import java.util.*

object TournamentHandler : Ticked {

    override fun tick(vararg args: Int) {
        val calendar = Calendar.getInstance()
        val wipeDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDay = app.wipeDate.get(Calendar.DAY_OF_MONTH)
        if (currentDay - wipeDay % 3 == 0 && wipeDay != currentDay && !isTournament) {
            isTournament = true
            println("НАЧАЛО ТУРНИРА")
        }
    }
}
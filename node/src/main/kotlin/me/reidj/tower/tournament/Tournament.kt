package me.reidj.tower.tournament

import me.reidj.tower.app
import me.reidj.tower.user.User
import java.util.*

class Tournament(val ratingType: RatingType, var tournamentMaxWavePassed: MutableList<Int>) {

    fun end(user: User) = run { tournamentMaxWavePassed.add(user.wave!!.level) }

    private val wipeDay = app.wipeDate.get(Calendar.DAY_OF_MONTH)

    fun isTournamentStarted() = run {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        currentDay - wipeDay % 3 == 0 && wipeDay != currentDay
    }
}

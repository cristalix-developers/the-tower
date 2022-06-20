package me.reidj.tower.tournament

import me.reidj.tower.app
import me.reidj.tower.user.User
import java.util.*

class Tournament(val ratingType: RatingType, var maxWavePassed: Int, var wavePassed: MutableList<Int>) {

    fun end(user: User) = run {
        wavePassed.add(user.wave!!.level)
        maxWavePassed = Collections.max(wavePassed)
    }

    @Transient
    private val wipeDay = app.wipeDate.get(Calendar.DAY_OF_MONTH)

    fun isTournamentStarted() = run {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        currentDay - wipeDay % 3 == 0 && wipeDay != currentDay
    }
}

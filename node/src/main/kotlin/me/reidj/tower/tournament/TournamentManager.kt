package me.reidj.tower.tournament

import me.reidj.tower.app
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

object TournamentManager {

    private const val COUNTDOWN_DAYS = 3

    private val startDate = Date(2022, 7, 2)

    fun end(user: User) = user.run {
        tournament.wavePassed.add(user.wave!!.level)
        tournament.maxWavePassed = Collections.max(tournament.wavePassed)
    }

    fun getOnlinePlayers() = Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }.filter { it.inGame }

    fun isTournamentDay() =
        TimeUnit.DAYS.convert(System.currentTimeMillis() - startDate.time, TimeUnit.MILLISECONDS) % COUNTDOWN_DAYS == 0L

    fun getTimeBefore(unit: TimeUnit): Long {
        if (isTournamentDay()) return 0
        var nextTournament = startDate.time
        val now = System.currentTimeMillis()
        while (nextTournament < now) {
            nextTournament += COUNTDOWN_DAYS * 86_400 * 1_000
        }
        return unit.convert(nextTournament - now, TimeUnit.MILLISECONDS)
    }

    fun getTimeAfter(unit: ChronoUnit) = unit.between(LocalTime.now(), LocalTime.MAX)
}

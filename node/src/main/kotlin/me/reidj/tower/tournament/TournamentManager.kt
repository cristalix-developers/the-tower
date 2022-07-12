package me.reidj.tower.tournament

import me.reidj.tower.user.User
import org.bukkit.Bukkit
import ru.kdev.simulatorapi.listener.SessionListener
import java.util.*
import java.util.concurrent.TimeUnit

object TournamentManager {

    private const val COUNTDOWN_DAYS = 3

    private val startDate = Date(2022, 6, 3)

    fun end(user: User) = user.run {
        tournament.wavePassed.add(user.wave!!.level)
        tournament.maxWavePassed = Collections.max(tournament.wavePassed)
    }

    fun getOnlinePlayers() = Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId) }.filter { it.inGame }

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

    fun getTimeAfter(unit: TimeUnit) =
        unit.convert((COUNTDOWN_DAYS - 1) * 86_400_000 - getTimeBefore(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
}

package me.reidj.tower.tournament

import me.reidj.tower.getUser
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

object TournamentManager {

    private const val COUNTDOWN_DAYS = 3L

    private val startDate = LocalDate.of(2022, 7, 25)

    fun end(user: User) = user.run {
        tournament.wavePassed.add(user.wave!!.level)
        tournament.maxWavePassed = Collections.max(tournament.wavePassed)
    }

    suspend fun getOnlinePlayers() = Bukkit.getOnlinePlayers().mapNotNull { getUser(it) }.filter { it.inGame }

    suspend fun getTournamentPlayers() = getOnlinePlayers().filter { it.isTournament }.size

    fun isTournamentDay() =
        TimeUnit.DAYS.convert(
            System.currentTimeMillis() - startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
            TimeUnit.MILLISECONDS
        ) % COUNTDOWN_DAYS == 0L

    fun getTimeBefore(): Duration {
        if (isTournamentDay()) return Duration.ZERO
        var duration = Duration.ZERO
        var i = 1
        while (duration.isNegative || duration.isZero) {
            duration = Duration.between(LocalDateTime.now(), startDate.plusDays(COUNTDOWN_DAYS * i).atStartOfDay())
            i++
        }
        return duration
    }

    fun getTimeAfter(unit: ChronoUnit) = unit.between(LocalTime.now(), LocalTime.MAX)
}

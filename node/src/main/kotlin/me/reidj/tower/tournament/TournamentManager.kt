package me.reidj.tower.tournament

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import me.reidj.tower.app
import me.reidj.tower.clientSocket
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.protocol.TopPackage
import me.reidj.tower.rank.RankManager
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

object TournamentManager : ClockInject {

    private const val COUNTDOWN_DAYS = 3L

    private val startDate = LocalDate.of(2022, 7, 24)

    private var tournamentHasStarted = false

    fun end(user: User) = user.run {
        stat.tournament.passedWaves.add(user.wave!!.level)
        stat.tournamentMaximumWavePassed = Collections.max(stat.tournament.passedWaves)
        if (tournamentHasStarted && LocalTime.now().hour >= 24 && getTournamentPlayers() == 0) {
            endOfTournament()
        }
    }

    fun getOnlinePlayers() = Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }.filter { it.inGame }

    fun getTournamentPlayers() = getOnlinePlayers().filter { it.isTournament }.size

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
    override fun run(tick: Int) {
        val now = LocalTime.now()
        if (isTournamentDay() && now.hour == 23 && now.minute == 59 && now.second == 59 && getTournamentPlayers() == 0) {
            println(12111)
            endOfTournament()
        }
    }

    fun endOfTournament() {
        CoroutineScope(Dispatchers.IO).launch {
            val sortAscending = clientSocket.writeAndAwaitResponse<TopPackage>(
                TopPackage(
                    "tournamentMaximumWavePassed",
                    1,
                    true
                )
            ).await()
            val sortDescending = clientSocket.writeAndAwaitResponse<TopPackage>(
                TopPackage(
                    "tournamentMaximumWavePassed",
                    1,
                    false
                )
            ).await()
            RankManager.changeRank(sortAscending, sortDescending)
        }
    }
}

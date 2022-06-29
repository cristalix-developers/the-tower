package me.reidj.tower.tournament

import me.reidj.tower.app
import me.reidj.tower.user.User
import org.bukkit.Bukkit
import ru.kdev.simulatorapi.listener.SessionListener
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*

object TournamentManager {

    fun end(user: User) = user.run {
        tournament.wavePassed.add(user.wave!!.level)
        tournament.maxWavePassed = Collections.max(tournament.wavePassed)
    }

    private val wipeDay = app.wipeDate.get(Calendar.DAY_OF_MONTH)

    fun isTournamentStarted() =
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH).run { this - wipeDay % 3 == 0 && wipeDay != this }

    fun hoursEndOfTournament() = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.MAX)

    fun getOnlinePlayers() = Bukkit.getOnlinePlayers().mapNotNull { SessionListener.simulator.getUser<User>(it.uniqueId) }.filter { it.inGame }

    /*fun hoursStartOfTournament() {
        Duration.between(LocalTime.now(), ).toHours()
    }*/
}

/*
import java.util.*
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val startDate = Date(2022, 6, 30, 0, 0)
    val countdownDays = 3

    fun isTournamentDay() =
        TimeUnit.DAYS.convert(System.currentTimeMillis() - startDate.time, TimeUnit.MILLISECONDS) % countdownDays == 0L

    fun isTournamentDayTest(time: Long) =
        TimeUnit.DAYS.convert(time - startDate.time, TimeUnit.MILLISECONDS) % countdownDays == 0L

    fun getTimeBefore(unit: TimeUnit): Long {
        if (isTournamentDay()) return 0
        var nextTournament = startDate.time
        val now = System.currentTimeMillis()
        while (nextTournament < now) {
            nextTournament += countdownDays * 86_400 * 1_000
        }
        return unit.convert(nextTournament - now, TimeUnit.MILLISECONDS)
    }

    fun getTimeAfter(unit: TimeUnit) = unit.convert((countdownDays - 1) * 86_400_000 - getTimeBefore(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)

    fun getTimeBeforeTest(now: Long, unit: TimeUnit): Long {
        if (isTournamentDayTest(now)) return 0
        var nextTournament = startDate.time
        while (nextTournament < now) {
            nextTournament += countdownDays * 86_400 * 1_000
        }
        return unit.convert(nextTournament - now, TimeUnit.MILLISECONDS)
    }

    fun getTimeAfterTest(now: Long, unit: TimeUnit) = unit.convert((countdownDays - 1) * 86_400_000 - getTimeBeforeTest(now, TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)

    repeat(30) { day ->
        val today = Date(2022, 7, day).time
        println("today $day ${isTournamentDayTest(today)}" )
        if (!isTournamentDayTest(today))
            repeat(24) { hour ->
                println("   hour $hour before ${getTimeBeforeTest(today + hour * 3_600 * 1_000L, TimeUnit.HOURS)} after ${getTimeAfterTest(today + hour * 3_600 * 1_000L, TimeUnit.HOURS)}")
            }
    }
}
 */
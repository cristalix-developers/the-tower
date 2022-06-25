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
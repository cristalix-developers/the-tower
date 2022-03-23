package me.reidj.tower.user

import me.reidj.tower.wave.Wave
import ru.kdev.simulatorapi.common.SimulatorUser
import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
class User(stat: Stat?): SimulatorUser(stat?.id!!) {

    var stat: Stat
    var wave: Wave? = null

    init {
        if (stat == null) {
            this.stat = Stat(
                UUID.randomUUID(),
                0
            )
        } else {
            this.stat = stat
        }
    }
}
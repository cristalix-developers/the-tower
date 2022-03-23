package me.reidj.tower.user

import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
class User(stat: Stat?) {

    var stat: Stat

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
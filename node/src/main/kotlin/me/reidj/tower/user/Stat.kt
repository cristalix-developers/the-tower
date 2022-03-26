package me.reidj.tower.user

import me.reidj.tower.pumping.PumpingType
import java.util.*

/**
 * @project tower
 * @author Рейдж
 */
data class Stat(
    val id: UUID,

    var maxWavePassed: Int,
    var pumpingTypes: MutableMap<String, PumpingType>
)

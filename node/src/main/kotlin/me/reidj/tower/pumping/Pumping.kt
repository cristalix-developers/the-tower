package me.reidj.tower.pumping

/**
 * @project tower
 * @author Рейдж
 */
class Pumping(private val pumpingType: PumpingType, var level: Int) {

    fun getValue(): Double = pumpingType.value + pumpingType.step * level
}
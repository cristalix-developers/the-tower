package me.reidj.tower.laboratory

/**
 * @project tower
 * @author Рейдж
 */
class Research(private val upgradeType: ResearchType, var level: Int, var whenBought: Int?) {

    fun getValue(): Double = upgradeType.value + upgradeType.step * level
}
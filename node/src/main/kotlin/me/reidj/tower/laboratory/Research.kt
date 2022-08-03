package me.reidj.tower.laboratory

/**
 * @project tower
 * @author Рейдж
 */
class Research(private val researchType: ResearchType, var level: Int, var whenBought: Double?) {

    fun getValue() = researchType.value + researchType.step * level
}
package me.reidj.tower.data

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Improvement(private val objectName: String, var level: Int) {

    fun getValue(): Double {
        val improvementType = ImprovementType.valueOf(objectName)
        return improvementType.value + improvementType.step * level
    }
}

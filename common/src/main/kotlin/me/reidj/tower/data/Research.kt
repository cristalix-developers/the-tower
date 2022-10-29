package me.reidj.tower.data

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Research(private val objectName: String, var level: Int, var whenBought: Long = 0) {

    fun getValue() = ResearchType.valueOf(objectName).let { it.value + it.step * level }

    fun getFullDuration() = ResearchType.valueOf(objectName).duration
}

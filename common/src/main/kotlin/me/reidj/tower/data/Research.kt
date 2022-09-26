package me.reidj.tower.data

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Research(private val objectName: String, var level: Int, var whenBought: Long = 0) {

    fun getValue(): Double {
        val researchType = ResearchType.valueOf(objectName)
        return researchType.value + researchType.step * level
    }

    fun getFullDuration() = ResearchType.valueOf(objectName).duration
}

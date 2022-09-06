package me.reidj.tower.data

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Research(private val objectName: String, var level: Int, var whenBought: Double?) {

    fun getValue(): Double {
        val researchType = ResearchType.valueOf(objectName)
        if (researchType.value == 0.0)
            return 0.0
        return researchType.value + researchType.step * level
    }
}

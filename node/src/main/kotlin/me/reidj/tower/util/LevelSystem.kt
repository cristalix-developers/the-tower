package me.reidj.tower.util

import kotlin.math.sqrt

/**
 * @project : tower
 * @author : Рейдж
 **/
object LevelSystem {

    fun getRequiredExperience(forLevel: Int) = forLevel * forLevel - forLevel / 2

    fun getLevel(experience: Double) = ((sqrt(5.0) * sqrt(experience * 80 + 5) + 5) / 20).toInt() + 1
}
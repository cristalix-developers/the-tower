package me.reidj.tower.data

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Tournament(val rankType: RankType, var maximumWavePassed: Int, var passedWaves: MutableList<Int>)

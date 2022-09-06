package me.reidj.tower.data

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Tournament(val ratingType: RatingType, var maximumWavePassed: Int, var passedWaves: MutableList<Int>)

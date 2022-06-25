package me.reidj.tower.tournament

data class Tournament(val ratingType: RatingType, var maxWavePassed: Int, var wavePassed: MutableList<Int>)

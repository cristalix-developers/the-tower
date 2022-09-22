package me.reidj.tower.data

import me.reidj.tower.booster.BoosterInfo
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class Stat(
    val uuid: UUID,
    var money: Double,
    var experience: Double,
    var maxHealth: Double,
    var dailyClaimTimestamp: Double,
    var lastEnter: Double,
    var maximumWavePassed: Int,
    var tournamentMaximumWavePassed: Int,
    var rebirth: Int,
    var rewardStreak: Int,
    var gem: Int,
    var userImprovementType: MutableMap<ImprovementType, Improvement>,
    var towerImprovementType: MutableMap<ImprovementType, Improvement>,
    var researchType: MutableMap<ResearchType, Research>,
    var localBoosters: MutableList<BoosterInfo>,
    var donates: MutableSet<String>,
    var tournament: Tournament,
    var sword: String,
    var currentCubeTexture: String,
    var rank: RankType,
)

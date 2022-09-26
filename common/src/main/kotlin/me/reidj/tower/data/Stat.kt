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
    val userImprovementType: MutableMap<ImprovementType, Improvement>,
    val towerImprovementType: MutableMap<ImprovementType, Improvement>,
    val researchType: MutableMap<ResearchType, Research>,
    val localBoosters: MutableList<BoosterInfo>,
    val donates: MutableSet<String>,
    val tournament: Tournament,
    var sword: String,
    var currentCubeTexture: String,
    var currentSwordSkin: String,
    var rank: RankType,
)

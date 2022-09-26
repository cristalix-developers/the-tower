package me.reidj.tower.user

import me.reidj.tower.data.*
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
object DefaultElements {

    fun createNewUser(uuid: UUID) = Stat(
        uuid,
        0.0,
        0.0,
        5.0,
        0.0,
        0.0,
        0,
        0,
        0,
        0,
        0,
        ImprovementType.values().filter { it.userImprovement }.associateWith { Improvement(it.name, 1) }.toMutableMap(),
        ImprovementType.values().filter { !it.userImprovement }.associateWith { Improvement(it.name, 1) }.toMutableMap(),
        ResearchType.values().associateWith { Research(it.name, 1, 0) }.toMutableMap(),
        mutableListOf(),
        mutableSetOf("CRYSTAL"),
        Tournament(RankType.NONE,  mutableListOf()),
        "NONE",
        "CRYSTAL",
        "MITHRIL_SWORD",
        RankType.NONE,
    )
}
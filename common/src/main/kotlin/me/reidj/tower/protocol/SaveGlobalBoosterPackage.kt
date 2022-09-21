package me.reidj.tower.protocol

import me.reidj.tower.booster.BoosterInfo
import ru.cristalix.core.network.CorePackage

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class SaveGlobalBoosterPackage(val booster: BoosterInfo) : CorePackage()

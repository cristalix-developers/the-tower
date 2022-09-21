package me.reidj.tower.protocol

import me.reidj.tower.booster.BoosterInfo
import ru.cristalix.core.network.CorePackage

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class RequestGlobalBoostersPackage : CorePackage() {
    lateinit var boosters: MutableList<BoosterInfo>
}

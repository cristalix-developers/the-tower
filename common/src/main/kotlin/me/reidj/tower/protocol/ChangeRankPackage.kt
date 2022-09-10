package me.reidj.tower.protocol

import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class ChangeRankPackage(val uuid: UUID, val isSortAscending: Boolean) : CorePackage()

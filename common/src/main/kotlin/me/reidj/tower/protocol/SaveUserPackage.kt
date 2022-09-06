package me.reidj.tower.protocol

import me.reidj.tower.data.Stat
import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : tower
 * @author : Рейдж
 **/
data class SaveUserPackage(
    val uuid: UUID,
    val userInfo: Stat
): CorePackage()

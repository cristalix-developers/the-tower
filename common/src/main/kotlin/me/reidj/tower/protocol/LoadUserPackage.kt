package me.reidj.tower.protocol

import me.reidj.tower.data.Stat
import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : tower
 * @author : Рейдж
 **/
data class LoadUserPackage(val uuid: UUID): CorePackage() {
    var stat: Stat? = null
}

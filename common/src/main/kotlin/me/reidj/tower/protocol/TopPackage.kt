package me.reidj.tower.protocol

import me.reidj.tower.top.PlayerTopEntry
import ru.cristalix.core.network.CorePackage

data class TopPackage(val topType: String, val limit: Int, val isSortAscending: Boolean): CorePackage() {

    lateinit var entries: List<PlayerTopEntry<Any>>
}

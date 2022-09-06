package me.reidj.tower.top

import me.reidj.tower.data.Stat


class PlayerTopEntry<V>(stat: Stat, value: V) : TopEntry<Stat, V>(stat, value) {
    var userName: String? = null
    var displayName: String? = null
}
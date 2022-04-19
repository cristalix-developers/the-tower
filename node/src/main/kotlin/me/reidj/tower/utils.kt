package me.reidj.tower

import org.bukkit.entity.Player

fun Player.flying(state: Boolean = true) = apply {
    allowFlight = state
    isFlying = state
}
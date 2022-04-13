package me.reidj.tower

import org.bukkit.entity.Player

fun Player.setFlying(state: Boolean = true) = apply {
    allowFlight = state
    isFlying = state
}
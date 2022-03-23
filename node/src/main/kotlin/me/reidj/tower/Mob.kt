package me.reidj.tower

import org.bukkit.entity.EntityType

/**
 * @project tower
 * @author Рейдж
 */
data class Mob(val hp: Int, val x: Double, val y: Double, val z: Double, val damage: Double, val type: EntityType)
//TODO Отправляю UUID type x y z

package me.reidj.tower.data

import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class Category(
    val title: String,
    val texture: String
) {
    ATTACK_IMPROVEMENTS("Улучшения атаки", "${PATH}slash.png"),
    PROTECTION_IMPROVEMENTS("Улучшения защиты", "${PATH}protection.png"),
    USEFUL_IMPROVEMENTS("Улучшения полезности", "${PATH}incentive.png"),
}
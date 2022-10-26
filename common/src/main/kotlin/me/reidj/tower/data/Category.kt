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
    ATTACK_IMPROVEMENTS("§aУлучшения атаки", "${PATH}slash.png"),
    PROTECTION_IMPROVEMENTS("§aУлучшения защиты", "${PATH}protection.png"),
    USEFUL_IMPROVEMENTS("§aУлучшения пользы", "${PATH}incentive.png"),
}
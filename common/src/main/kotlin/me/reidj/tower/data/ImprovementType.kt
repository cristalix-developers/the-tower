package me.reidj.tower.data

import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class ImprovementType(
    val description: String,
    val texture: String,
    var value: Double,
    var price: Double,
    var step: Double,
    var userImprovement: Boolean
) : Pumping {
    DAMAGE(
        "Урон §3+6",
        "${PATH}damage.png",
        -5.0,
        10.0,
        6.0,
    false
    ),
    BULLET_DELAY(
        "Перезарядка §3+0.004",
        "${PATH}bullet_delay.png",
        .1,
        8.0,
        -.004,
        false
    ),
    HEALTH(
        "Здоровье §3+1",
        "${PATH}health.png",
        4.0,
        5.0,
        1.0,
        false
    ),
    REGEN(
        "Регенерация §3+0.1",
        "${PATH}regeneration.png",
        -0.1,
        30.0,
        .1,
        false
    ),
    PROTECTION(
        "Защита §3+0.1",
        "${PATH}protection.png",
        -0.1,
        40.0,
        0.1,
        false
    ),
    RADIUS(
        "Радиус атаки §3+0.1",
        "${PATH}radius.png",
        9.9,
        3.0,
        0.1,
        false
    ),
    CRITICAL_STRIKE_CHANCE(
        "Шанс крит.удара §3+1",
        "minecraft:mcpatcher/cit/simulators/critical_damage.png",
        1.0,
        33.0,
        1.0,
        false
    ),
    CRITICAL_HIT_RATIO(
        "Коэффициент крит.удара §3+0.10",
        "minecraft:mcpatcher/cit/simulators/critical_hit_ratio.png",
        1.0,
        33.0,
        0.10,
        false
    ),
    SWORD(
        "Улучшение меча",
        "${PATH}sword.png",
        -1.0,
        10.0,
        1.0,
        true
    ),
    CASH_BONUS_KILL(
        "Бонус за убийство §3+1",
        "${PATH}kill_bonus.png",
        0.0,
        130.0,
        1.0,
        true
    ),
    CASH_BONUS_WAVE_PASS(
        "Бонус за волну §3+1",
        "${PATH}cash_bonus_wave_pass.png",
        0.0,
        160.0,
        1.0,
        true
    ),
    ;
}
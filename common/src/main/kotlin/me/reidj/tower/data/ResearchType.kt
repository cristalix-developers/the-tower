package me.reidj.tower.data

import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class ResearchType(
    var title: String,
    val texture: String,
    var price: Int,
    var duration: Int,
    var value: Double,
    val step: Double,
    val category: Category
) : Pumping {
    DISCOUNT(
        "Скидка на монеты лаборатории",
        "${PATH}discount.png",
        40,
        19,
        -0.30,
        0.30,
        Category.USEFUL_IMPROVEMENTS
    ),
    LABORATORY_SPEED(
        "Скорость лаборатории",
        "${PATH}laboratory_speed.png",
        40,
        24,
        -1.02,
        1.02,
        Category.USEFUL_IMPROVEMENTS
    ),
    INITIAL_MONEY(
        "Начальная сумма токенов",
        "${PATH}initial_money.png",
        30,
        14,
        -5.0,
        5.0,
        Category.USEFUL_IMPROVEMENTS
    ),
    CASH_BONUS_WAVE_PASS(
        "Бонус токенов за волну",
        "${PATH}token_wave.png",
        40,
        19,
        -1.02,
        1.02,
        Category.USEFUL_IMPROVEMENTS
    ),
    CASH_BONUS_KILL(
        "Бонус токенов за убийство",
        "${PATH}token_bonus.png",
        40,
        19,
        -1.02,
        1.02,
        Category.USEFUL_IMPROVEMENTS
    ),
    MONEY_BONUS_WAVE_PASS(
        "Денежный бонус за волну",
        "${PATH}money_bonus.png",
        40,
        19,
        -1.02,
        1.02,
        Category.USEFUL_IMPROVEMENTS
    ),
    DAMAGE(
        "Урон",
        "${PATH}damage.png",
        30,
        14,
        -1.02,
        1.02,
        Category.ATTACK_IMPROVEMENTS
    ),
    HEALTH(
        "Здоровье",
        "${PATH}health.png",
        30,
        14,
        -1.03,
        1.03,
        Category.PROTECTION_IMPROVEMENTS
    ),
    PROTECTION(
        "Защита",
        "${PATH}protection.png",
        30,
        15,
        -1.03,
        1.03,
        Category.PROTECTION_IMPROVEMENTS
    ),
    BULLET_DELAY(
        "Перезарядка",
        "${PATH}bullet_delay.png",
        30,
        14,
        -1.02,
        1.02,
        Category.ATTACK_IMPROVEMENTS
    ),
    CRITICAL_HIT(
        "Коэффициент крит.удара",
        "${PATH}critical_hit_ratio.png",
        30,
        14,
        -1.03,
        1.03,
        Category.ATTACK_IMPROVEMENTS
    ),
    ;
}
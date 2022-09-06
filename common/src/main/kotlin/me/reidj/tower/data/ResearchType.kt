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
) : Pumping {
    DISCOUNT("Скидка на монеты лаборатории", "${PATH}discount.png", 40, 19, 0.0, 0.30),
    LABORATORY_SPEED("Скорость лаборатории", "${PATH}laboratory_speed.png", 40, 24, 0.0, 1.02),
    INITIAL_MONEY("Начальная сумма токенов", "${PATH}initial_money.png", 30, 14, 0.0, 5.0),
    CASH_BONUS_WAVE_PASS("Бонус токенов за волну", "${PATH}token_wave.png", 40, 19, 0.0, 1.02),
    CASH_BONUS_KILL("Бонус токенов за убийство", "${PATH}token_bonus.png", 40, 19, 0.0, 1.02),
    MONEY_BONUS_WAVE_PASS("Денежный бонус за волну", "${PATH}money_bonus.png", 40, 19, 0.0, 1.02),
    DAMAGE("Урон", "${PATH}damage.png", 30, 14, 0.0, 1.02),
    HEALTH("Здоровье", "${PATH}health.png", 30, 14, 0.0, 1.03),
    PROTECTION("Защита", "${PATH}protection.png", 30, 15, 0.0, 1.03),
    BULLET_DELAY("Перезарядка", "${PATH}bullet_delay.png", 30, 14, 0.0, 1.02),
    CRITICAL_HIT("Коэффициент крит.удара", "${PATH}critical_hit_ratio.png", 30, 14, 0.0, 1.03),
    ;
}
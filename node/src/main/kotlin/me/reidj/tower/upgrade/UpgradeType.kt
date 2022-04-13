package me.reidj.tower.upgrade

/**
 * @project tower
 * @author Рейдж
 */
enum class UpgradeType(
        val title: String,
        val lore: String,
        val nbt: String,
        var value: Double,
        var price: Int,
        val step: Double
) {
    DAMAGE("Урон", "Увеличивает максимально наносимый урон\n§7на 6 единиц", "other:custom_sword", 1.0, 10, 6.0),
    BULLET_DELAY(
            "Перезарядка",
            "Уменьшает время перезарядки\n§7на 0.004 единицы",
            "simulators:save_crystal",
            .1,
            8,
            -.004
    ),
    HEALTH("Здоровье", "Увеличивает максимальное здоровье\n§7на 1 единицу", "other:love", 4.0, 5, 1.0),
    REGEN(
            "Регенерация здоровья",
            "Увеличивает регенирацию здоровья\n§7на 0.1 единицу/сек",
            "other:heart",
            .0,
            30,
            .1
    ),
    PROTECTION("Защита", "Увеличивает защиту\n§7на 0.1 единицу", "other:guild", 0.0, 40, 0.1),
    CASH_BONUS_KILL(
            "Денежный бонус за убийство",
            "Увеличивает количество получаемых жетонов\n§7на 1 единицу",
            "other:bank",
            0.00,
            130,
            1.0
    ),
    CASH_BONUS_WAVE_PASS(
            "Денежный бонус за пройденную волну",
            "Увеличивает количество получаемых жетонов\n§7на 1 единицу",
            "other:bag1",
            0.00,
            160,
            1.0
    )
    ;
}
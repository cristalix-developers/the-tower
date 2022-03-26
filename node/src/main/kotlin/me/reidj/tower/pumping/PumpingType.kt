package me.reidj.tower.pumping

/**
 * @project tower
 * @author Рейдж
 */
enum class PumpingType(
    val title: String,
    val lore: String,
    val nbt: String,
    var upgradable: Double,
    var level: Int,
    var startPrice: Int,
    val step: Double
) {
    DAMAGE("Урон", "dd", "other:custom_sword", 1.0, 1, 450, 6.0),
    ATTACK_SPEED("Скорость атаки", "ss", "simulators:save_crystal", 1.0, 1, 550, .5),
    HEALTH("Здоровье", "hh", "other:guild", 5.0, 1, 450, 1.0),
    ;
}
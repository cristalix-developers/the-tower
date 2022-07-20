package me.reidj.tower.upgrade

import me.func.mod.util.nbt
import me.reidj.tower.item
import org.bukkit.inventory.ItemStack

/**
 * @project tower
 * @author Рейдж
 */
enum class UpgradeType(
    val lore: String,
    val item: ItemStack,
    var value: Double,
    var price: Int,
    val step: Double,
    val isUserUpgrade: Boolean
) {
    DAMAGE("Урон §3+6", item {}.nbt("other", "custom_sword"), -5.0, 10, 6.0, false),
    BULLET_DELAY("Перезарядка §3+0.004", item {}.nbt("simulators", "save_crystal"), .1, 8, -.004, false),
    HEALTH("Здоровье §3+1", item {}.nbt("other", "love"), 4.0, 5, 1.0, false),
    REGEN("Регенерация §3+0.1", item {}.nbt("other", "heart"), -0.1, 30, .1, false),
    SWORD("Улучшение меча", item {}.nbt("simulators", "gungame"), -1.0, 10, 1.0, true),
    PROTECTION("Защита §3+0.1", item {}.nbt("other", "guild"), -0.1, 40, 0.1, false),
    RADIUS("Радиус атаки §3+0.1", item {}.nbt("simulators", "smash"), 9.9, 3, 0.1, false),
    CRITICAL_STRIKE_CHANCE("Шанс крит.удара §3+1", item {}.nbt("simulators", "critical_damage"), 1.0, 33, 1.0, false),
    CRITICAL_HIT_RATIO("Коэффициент крит.удара §3+0.10", item {}.nbt("simulators", "critical_hit_ratio"), 1.0, 33, 0.10, false),
    CASH_BONUS_KILL("Бонус за убийство §3+1", item {}.nbt("other", "bank"), 0.0, 130, 1.0, true),
    CASH_BONUS_WAVE_PASS("Бонус за волну §3+1", item {}.nbt("other", "bag1"), 0.0, 160, 1.0, true),
    ;
}
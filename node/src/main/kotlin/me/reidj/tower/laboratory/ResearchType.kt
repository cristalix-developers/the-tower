package me.reidj.tower.laboratory

import me.func.mod.util.nbt
import me.reidj.tower.item
import me.reidj.tower.user.Upgrade
import org.bukkit.inventory.ItemStack

/**
 * @project tower
 * @author Рейдж
 */
enum class ResearchType(
    var title: String,
    val item: ItemStack,
    var price: Int,
    var duration: Int,
    var value: Double,
    val step: Double,
): Upgrade {
    DISCOUNT("Скидка на монеты лаборатории", item {  }, 40, 19, 0.0, 0.30),
    LABORATORY_SPEED("Скорость лаборатории", item {  }, 40, 24, 1.0, 1.02),
    DAMAGE("Урон", item {}.nbt("other", "custom_sword"), 30, 14, 1.0, 1.02),
    BULLET_DELAY("Перезарядка", item {}.nbt("simulators", "save_crystal"), 30, 14, 1.0, 1.02),
    CRITICAL_HIT("Коэффициент крит.удара", item {  }, 30, 14, 1.0, 1.03),
    CASH_BONUS_WAVE_PASS("Бонус за волну", item {}.nbt("other", "bag1"), 40, 19, 1.0, 1.02),
    CASH_BONUS_KILL("Бонус за убийство", item {}.nbt("other", "bank"), 40, 19, 1.0, 1.02),
}
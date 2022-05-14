package me.reidj.tower.upgrade

import me.reidj.tower.item
import me.reidj.tower.nbt
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
    val step: Double
) {
    DAMAGE("Урон §3+6", item { nbt("other", "custom_sword") }, -5.0, 10, 6.0),
    BULLET_DELAY("Перезарядка §3+0.004", item { nbt("simulators", "save_crystal") }, .1, 8, -.004),
    HEALTH("Здоровье §3+1", item { nbt("other", "love") }, 4.0, 5, 1.0),
    REGEN("Регенерация §3+0.1", item { nbt("other", "heart") }, -0.1, 30, .1),
    SWORD("Улучшение меча", item { nbt("other", "custom_sword") }, -1.0, 10, 1.0),
    PROTECTION("Защита §3+0.1", item { nbt("other", "guild") }, -0.1, 40, 0.1),
    RADIUS("Радиус атаки §3+0.1", item { nbt("other", "love") }, 9.9, 3, 0.1),
    CASH_BONUS_KILL("Бонус за убийство §3+1", item { nbt("other", "bank") }, 0.0, 130, 1.0),
    CASH_BONUS_WAVE_PASS("Бонус за волну §3+1", item { nbt("other", "bag1") }, 0.0, 160, 1.0),
    ;
}
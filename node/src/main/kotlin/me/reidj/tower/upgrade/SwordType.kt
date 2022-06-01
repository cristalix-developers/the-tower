package me.reidj.tower.upgrade

import me.reidj.tower.item
import me.reidj.tower.text
import me.reidj.tower.user.Upgradable
import me.reidj.tower.user.User
import org.bukkit.Material

enum class SwordType(
    private val title: String,
    private val material: Material,
    val damage: Double,
) : Upgradable {
    NONE("", Material.AIR, 0.0),
    STICK("§7Палка", Material.STICK, 1.0),
    TRAINING_SWORD("§3Тренировочный меч", Material.WOOD_SWORD, 3.0),
    STONE_SWORD("§aКаменный меч", Material.STONE_SWORD, 5.0),
    IRON_SWORD("§5Железный меч", Material.IRON_SWORD, 7.0),
    DIAMOND_SWORD("§6Алмазный меч", Material.DIAMOND_SWORD, 9.0),
    ;

    override fun update(user: User, vararg type: UpgradeType) {
        user.apply {
            val level = upgradeTypes[UpgradeType.SWORD]!!.getValue()
            if ((level == 1.0 || level % 25 == 0.0) && getNext() != null)
                sword = getNext()!!
        }
    }

    fun giveSword(user: User) = user.apply {
        if (sword.material != Material.AIR)
            player!!.inventory.setItem(0, item(sword.material) { text(sword.title) })
    }

    fun update(user: User) = update(user, UpgradeType.SWORD)

    open fun getNext(): SwordType? =
        if (ordinal >= SwordType.values().size - 1) null else SwordType.values()[ordinal + 1]
}
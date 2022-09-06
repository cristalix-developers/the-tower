package me.reidj.tower.sword

import dev.implario.bukkit.item.item
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.Pumping
import me.reidj.tower.upgrade.Upgradable
import me.reidj.tower.user.User
import org.bukkit.Material

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class SwordType(private val title: String, private val material: Material, val damage: Double) : Upgradable {
    NONE("", Material.AIR, 0.0),
    STICK("§7Палка", Material.STICK, 1.0),
    TRAINING_SWORD("§3Тренировочный меч", Material.WOOD_SWORD, 3.0),
    STONE_SWORD("§aКаменный меч", Material.STONE_SWORD, 5.0),
    IRON_SWORD("§5Железный меч", Material.IRON_SWORD, 7.0),
    DIAMOND_SWORD("§6Алмазный меч", Material.DIAMOND_SWORD, 9.0),
    ;

    override fun update(user: User, vararg pumping: Pumping) = user.run {
        val level = stat.userImprovementType[ImprovementType.SWORD]!!.level
        if ((level == 1 || level % 25 == 0) && getNext() != null)
            stat.sword = getNext()!!.name
    }

    fun giveSword(user: User) = user.run {
        if (material != Material.AIR)
            player.inventory.setItem(0, item {
                type = material
                text(title)
            })
    }

    fun update(user: User) = update(user, ImprovementType.SWORD)

    open fun getNext(): SwordType? =
        if (ordinal >= SwordType.values().size - 1) null else SwordType.values()[ordinal + 1]
}
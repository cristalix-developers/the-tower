package me.reidj.tower.sword

import dev.implario.bukkit.item.item
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.donate.SwordSkin
import me.reidj.tower.user.User
import me.reidj.tower.util.plural
import org.bukkit.Material

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class SwordType(private val title: String, private val material: Material, val damage: Double) {
    NONE("", Material.AIR, 0.0),
    STICK("§7Палка", Material.STICK, 1.0),
    TRAINING_SWORD("§3Тренировочный меч", Material.DIAMOND_SWORD, 3.0),
    STONE_SWORD("§aКаменный меч", Material.DIAMOND_SWORD, 5.0),
    IRON_SWORD("§5Железный меч", Material.DIAMOND_SWORD, 7.0),
    DIAMOND_SWORD("§6Алмазный меч", Material.DIAMOND_SWORD, 9.0),
    ;

    fun swordImprove(user: User) = user.run {
        val level = stat.userImprovementType[ImprovementType.SWORD]!!.level
        if ((level == 1 || level % 25 == 0) && getNext() != null)
            stat.sword = getNext()!!.name
    }

    fun giveSword(user: User) = user.run {
        val skin = SwordSkin.valueOf(user.stat.currentSwordSkin)
        if (material != Material.AIR)
            player.inventory.setItem(0, item {
                type = material
                text("""
                    ${if (material == Material.STICK) title else skin.getTitle()}
                    §7§o${damage.toInt()} ${damage.plural("урон", "урона", "урона")}
                """.trimIndent())
                nbt("HideFlags", 63)
                nbt("tower", skin.name.lowercase())
            })
    }

    fun update(user: User) = ModTransfer(damage).send("user:sword",user.player)

    open fun getNext(): SwordType? =
        if (ordinal >= SwordType.values().size - 1) null else SwordType.values()[ordinal + 1]
}
package me.reidj.tower

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.mob.Mob
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

val barrier = item(Material.BARRIER) {}

fun Player.flying(state: Boolean = true) = apply {
    allowFlight = state
    isFlying = state
}

fun ItemStack.text(value: String) = apply {
    val strings = value.replace('&', '§').split("\n")
    val meta = itemMeta
    meta.displayName = strings.first()
    meta.lore = strings.drop(1).map { it.trimStart() }
    itemMeta = meta
}

fun ItemStack.data(value: Short) = apply { durability = value }
fun item(material: Material, apply: ItemStack.() -> Unit): ItemStack =
    ItemStack(material).apply { apply.invoke(this) }

fun item(itemStack: ItemStack, apply: ItemStack.() -> Unit): Unit = apply.invoke(itemStack)
fun item(apply: ItemStack.() -> Unit): ItemStack = ItemStack(Material.CLAY_BALL).apply { apply.invoke(this) }

fun MutableList<Mob>.clear(player: Player) = apply {
    forEach { ModTransfer(it.uuid.toString(), "").send("mob:kill", player) }
    clear()
}

fun buyFailure(player: Player) = Anime.run {
    close(player)
    itemTitle(player, barrier, "Ошибка", "Недостаточно средств", 2.0)
}



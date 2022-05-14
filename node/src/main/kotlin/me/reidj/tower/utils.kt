package me.reidj.tower

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.flying(state: Boolean = true) = apply {
    allowFlight = state
    isFlying = state
}

fun ItemStack.text(value: String) = apply {
    val strings = value.replace('&', '§').split("\n")
    itemMeta.displayName = strings.first()
    lore = strings.drop(1).map { it.trimStart() }
    itemMeta = itemMeta
}
fun ItemStack.data(value: Short) = apply { durability = value }

fun item(material: Material, apply: ItemStack.() -> Unit): ItemStack = ItemStack(material).apply { apply.invoke(this) }

fun item(apply: ItemStack.() -> Unit): ItemStack = ItemStack(Material.CLAY_BALL).apply { apply.invoke(this) }
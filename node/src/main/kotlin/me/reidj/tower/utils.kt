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
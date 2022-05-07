package me.reidj.tower

import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

fun Player.flying(state: Boolean = true) = apply {
    allowFlight = state
    isFlying = state
}

fun command(name: String, consumer: (Player, Array<out String>) -> Unit) =
    Bukkit.getCommandMap().register("tower", object : Command(name) {
        override fun execute(sender: CommandSender, var2: String, agrs: Array<out String>): Boolean {
            if (sender is Player)
                consumer(sender, agrs)
            return true
        }

    })

fun App.after(ticks: Long, runnable: () -> Unit): BukkitTask =
    Bukkit.getScheduler().runTaskLater(this, { runnable.invoke() }, ticks)

fun App.listener(vararg listener: Listener) = listener.forEach { Bukkit.getPluginManager().registerEvents(it, this) }

fun ItemStack.nbt(key: String, value: String) = apply {
    if (tag == null)
        handle.tag = NBTTagCompound()
    tag.setString(key, value)
}

fun ItemStack.text(value: String) = apply {
    val strings = value.replace('&', '§').split("\n")
    displayName = value
    lore = strings.drop(1).map { it.trimStart() }
}

fun ItemStack.data(value: Short) = apply { durability = value }

fun item(material: Material, apply: ItemStack.() -> Unit): ItemStack = ItemStack(material).apply { apply.invoke(this) }

fun item(apply: ItemStack.() -> Unit): ItemStack = ItemStack(Material.CLAY_BALL).apply { apply.invoke(this) }
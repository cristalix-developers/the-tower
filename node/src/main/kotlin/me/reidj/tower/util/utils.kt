package me.reidj.tower.util

import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.game.wave.mob.Mob
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.transfer.ITransferService
import kotlin.math.abs

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

private val barrier = ItemStack(Material.BARRIER)
private val startItem = item {
    type(Material.CLAY_BALL)
    text("§bИграть")
    nbt("other", "guild_members")
    nbt("click", "play")
}
private var backItem = item {
    type(Material.CLAY_BALL)
    text("§cВыйти")
    nbt("other", "cancel")
    nbt("click", "leave")
}
private var settingsItem = item {
    type(Material.CLAY_BALL)
    text("§aПерсонаж")
    nbt("other", "clothes")
    nbt("click", "menu")
}

fun Player.flying(state: Boolean = true) = run {
    allowFlight = state
    isFlying = state
}

fun Player.giveDefaultItems() = kotlin.run {
    teleport(app.playerDataManager.spawn)
    inventory.run {
        clear()
        setItem(0, startItem)
        setItem(4, settingsItem)
        setItem(8, backItem)
    }
}

fun Player.transfer() = ITransferService.get().transfer(player.uniqueId, app.getHub())

fun MutableList<Mob>.clear(player: Player) = apply {
    forEach { ModTransfer(it.uuid.toString(), "").send("mob:kill", player) }
    clear()
}

 fun Player.error(subTitle: String) {
    Glow.animate(player, 2.0, GlowColor.RED)
    Anime.itemTitle(player, barrier, "Ошибка", subTitle, 2.0)
    Anime.close(player)
}

fun Double.plural(one: String, two: String, five: String): String {
    val n = abs(this) % 100
    val n1 = n % 10
    if (n in 11.0..20.0)
        return two
    else if (n1 in 2.0..4.0)
        return five
    else if (n1 == 1.0)
        return one
    return five
}
package me.reidj.tower.util

import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.func.protocol.GlowColor
import me.reidj.tower.app
import me.reidj.tower.game.wave.mob.Mob
import org.bukkit.Color
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

val navigationItem = item {
    type = Material.COMPASS
    text("&2Навигатор")
    nbt("click", "navigator")
}
val startGameItem = item {
    type = Material.COMPASS
    nbt("color", Color(255, 215, 0).asRGB())
    text("&bНачать играть")
    nbt("click", "play")
}
val donateItem = item {
    type = Material.CLAY_BALL
    nbt("skyblock", "donate")
    nbt("click", "donate")
    text("&eДонат")
}
val profileItem = item {
    type = Material.CLAY_BALL
    nbt("skyblock", "info")
    nbt("click", "menu")
    text("&2Профиль")
}
val hubItem = item {
    type = Material.CLAY_BALL
    text("&4Обратно в хаб")
    nbt("other", "arrow_back")
    nbt("click", "leave")
}

fun Player.flying(state: Boolean = true) = run {
    allowFlight = state
    isFlying = state
}

fun Player.giveDefaultItems() = kotlin.run {
    teleport(app.playerDataManager.spawn)
    inventory.run {
        clear()
        setItem(0, navigationItem)
        setItem(1, startGameItem)
        setItem(2, donateItem)
        setItem(4, profileItem)
        setItem(8, hubItem)
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
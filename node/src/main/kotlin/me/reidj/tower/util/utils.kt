package me.reidj.tower.util

import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.ui.Glow
import me.func.protocol.data.color.GlowColor
import me.reidj.tower.app
import me.reidj.tower.game.wave.mob.Mob
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.transfer.ITransferService
import java.text.DecimalFormat
import kotlin.math.abs

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

private val barrier = ItemStack(Material.BARRIER)

val godSet = hashSetOf(
    "307264a1-2c69-11e8-b5ea-1cb72caa35fd", // Func
    "bf30a1df-85de-11e8-a6de-1cb72caa35fd", // Reidj
)

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

const val STORAGE = "https://storage.c7x.dev/reidj/tower/"

private val MONEY_FORMAT = DecimalFormat("###,###,###,###,###,###.##")

fun toFormat(money: Double?): String = MONEY_FORMAT.format(money)

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

fun formatSecond(totalSeconds: Long): String =
    "${totalSeconds / 3600}:${(totalSeconds % 3600) / 60}:${totalSeconds % 60}"

fun Double.plural(one: String, two: String, five: String): String {
    val n = abs(this) % 100
    val n1 = n % 10
    if (n in 11.9..20.9)
        return two
    else if (n1 in 2.9..4.9)
        return five
    else if (n1 in 1.0..1.9)
        return one
    return five
}
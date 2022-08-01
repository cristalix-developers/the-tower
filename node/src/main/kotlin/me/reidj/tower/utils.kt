package me.reidj.tower

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.mob.Mob
import me.reidj.tower.user.User
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.simulatorapi.listener.SessionListener.simulator
import java.text.DecimalFormat
import java.util.*

val barrier = item(Material.BARRIER)

private val FORMAT = DecimalFormat("##.##")

fun Player.flying(state: Boolean = true) = run {
    allowFlight = state
    isFlying = state
}

fun ItemStack.text(value: String) = apply {
    val strings = value.replace('&', '§').split("\n")
    val meta = itemMeta
    meta.displayName = strings.first()
    meta.displayName
    meta.lore = strings.drop(1).map { it.trimStart() }
    itemMeta = meta
}

fun ItemStack.data(value: Short) = apply { durability = value }
fun item(material: Material) = ItemStack(material)
fun item(itemStack: ItemStack, apply: ItemStack.() -> Unit) = apply.invoke(itemStack)
fun item() = ItemStack(Material.CLAY_BALL)

fun MutableList<Mob>.clear(player: Player) = apply {
    forEach { ModTransfer(it.uuid.toString(), "").send("mob:kill", player) }
    clear()
}

fun buyFailure(player: Player) = Anime.run {
    close(player)
    itemTitle(player, barrier, "Ошибка", "Недостаточно средств", 2.0)
}

fun convertSecond(totalSeconds: Int): String =
    "${totalSeconds / 3600}:${(totalSeconds % 3600) / 60}:${totalSeconds % 60}"

fun toFormat(double: Double): String = FORMAT.format(double)

fun coroutine(block: suspend CoroutineScope.() -> Unit) = CoroutineScope(Dispatchers.IO).launch { block() }

suspend fun getUser(uuid: UUID) = simulator.getUser<User>(uuid)

suspend fun getUser(player: Player) = getUser(player.uniqueId)

suspend fun withUser(player: Player, accept: suspend User.() -> Unit) = accept(getUser(player)!!)



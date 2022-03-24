package me.reidj.tower.listener

import clepto.bukkit.B
import clepto.cristalix.Cristalix.transfer
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.HUB
import me.reidj.tower.app
import me.reidj.tower.user.User
import me.reidj.tower.wave.Wave
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import ru.cristalix.core.realm.RealmId

/**
 * @project tower
 * @author Рейдж
 */
object InteractEvent : Listener {

    private const val MOVE_SPEED: Double = .001
    private const val CONST_TICKS_BEFORE_STRIKE = 5
    private const val TICKS_BEFORE_STRIKE = 40

    init {
        B.regCommand({ player, _ ->
            transfer(listOf(player.uniqueId), RealmId.of(HUB))
            null
        }, "leave")
        B.regCommand({ player, _ ->
            player.apply {
                if (app.simulator.getUser<User>(player.uniqueId)?.inGame == true)
                    return@apply
                inventory.clear()
                teleport(app.gamePosition)

                // Отправляем точку башни
                ModTransfer(app.tower.x, app.tower.y, app.tower.z).send("tower:init", player)

                // Отправляем точки со спавнерами
                app.generators.forEach {
                    ModTransfer()
                        .double(it.x)
                        .double(it.y)
                        .double(it.z)
                        .send("mobs:init", player)
                }

                // Отправляю скорость передвижения моба
                ModTransfer().double(MOVE_SPEED).send("tower:mobspeed", player)

                ModTransfer().integer(TICKS_BEFORE_STRIKE).integer(CONST_TICKS_BEFORE_STRIKE).send("tower:strike", player)

                val user = app.simulator.getUser<User>(player.uniqueId)
                user?.inGame = true
                B.postpone(3 * 20) {
                    val wave = Wave(System.currentTimeMillis(), 0, mutableListOf(), player)
                    user?.wave = wave
                    wave.start()
                }
            }
            null
        }, "play")
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (item == null)
            return
        val nmsItem = CraftItemStack.asNMSCopy(item)
        if (nmsItem.hasTag() && nmsItem.tag.hasKeyOfType("click", 8))
            player.performCommand(nmsItem.tag.getString("click"))
    }
}
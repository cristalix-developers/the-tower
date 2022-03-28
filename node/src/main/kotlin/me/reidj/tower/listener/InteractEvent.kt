package me.reidj.tower.listener

import clepto.bukkit.B
import clepto.cristalix.Cristalix.transfer
import dev.implario.bukkit.item.item
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.HUB
import me.reidj.tower.app
import me.reidj.tower.mod.ModHelper
import me.reidj.tower.user.User
import me.reidj.tower.wave.Wave
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import ru.cristalix.core.realm.RealmId
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object InteractEvent : Listener {

    private const val MOVE_SPEED: Double = .01
    private const val CONST_TICKS_BEFORE_STRIKE = 20
    private const val TICKS_BEFORE_STRIKE = 40

    private val upgradeItem = item {
        type = Material.CLAY_BALL
        text("§bУлучшения")
        nbt("other", "guild_members")
        nbt("click", "settings")
    }

    init {
        B.regCommand({ player, _ ->
            transfer(listOf(player.uniqueId), RealmId.of(HUB))
            null
        }, "leave")
        B.regCommand({ player, _ ->
            player.apply {
                val user = SessionListener.simulator.getUser<User>(uniqueId)!!
                if (user.inGame)
                    return@apply
                inventory.clear()
                teleport(app.gamePosition)
                inventory.setItem(4, upgradeItem)

                // Отправляем точку башни
                ModTransfer(app.tower.x, app.tower.y, app.tower.z).send("tower:init", this)

                // Отправляем точки со спавнерами
                app.generators.forEach {
                    ModTransfer()
                        .double(it.x)
                        .double(it.y)
                        .double(it.z)
                        .send("mobs:init", this)
                }

                // Отправляю скорость передвижения моба
                ModTransfer().double(MOVE_SPEED).send("tower:mobspeed", this)

                ModHelper.updateBarVisible(player)

                // Отправляю количество пуль
                B.postpone(20) {
                    ModTransfer().integer(TICKS_BEFORE_STRIKE).integer(CONST_TICKS_BEFORE_STRIKE)
                        .send("tower:strike", this)
                }

                // Начинаю волну
                user.inGame = true
                user.giveTokens(80, true)
                user.health = 5
                user.maxHealth = 5
                ModHelper.updateHeartBar(user.health, user.maxHealth, player)
                B.postpone(3 * 20) {
                    val wave = Wave(true, System.currentTimeMillis(), 0, mutableListOf(), this)
                    user.wave = wave
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
        val tag = nmsItem.tag
        if (nmsItem.hasTag() && tag.hasKeyOfType("click", 8))
            player.performCommand(tag.getString("click"))
    }
}
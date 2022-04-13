package me.reidj.tower.listener

import clepto.bukkit.B
import clepto.cristalix.Cristalix.transfer
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.HUB
import me.reidj.tower.setFlying
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.Session
import me.reidj.tower.user.User
import me.reidj.tower.wave.Wave
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

    init {
        B.regCommand({ player, _ ->
            transfer(listOf(player.uniqueId), RealmId.of(HUB))
            null
        }, "leave")

        B.regCommand({ player, _ ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                if (inGame)
                    return@apply
                session = Session(tower.upgrades)
                session.upgrade.values.forEach { it.level = 1 }

                player.apply {
                    inventory.clear()
                    teleport(session.arenaSpawn)
                    inventory.setItem(4, UpgradeInventory.workshop)
                    setFlying()
                }

                tower.health = tower.maxHealth
                tower.updateHealth()
                tower.update(
                    this,
                    UpgradeType.BULLET_DELAY,
                    UpgradeType.DAMAGE,
                    UpgradeType.HEALTH,
                    UpgradeType.PROTECTION,
                    UpgradeType.REGEN
                )
                update(
                    this,
                    UpgradeType.CASH_BONUS_KILL,
                    UpgradeType.CASH_BONUS_WAVE_PASS,
                )

                // Отправляем точки со спавнерами
                session.generators.forEach { ModTransfer(it.x, it.y, it.z).send("mobs:init", player) }

                // Игра началась
                ModTransfer(
                    true,
                    session.cubeLocation.x,
                    session.cubeLocation.y,
                    session.cubeLocation.z,
                    MOVE_SPEED,
                    TICKS_BEFORE_STRIKE,
                    CONST_TICKS_BEFORE_STRIKE
                ).send("tower:update-state", player)

                // Начинаю волну
                inGame = true
                giveTokens(80)
                B.postpone(3 * 20) {
                    val current = Wave(true, System.currentTimeMillis(), 1, mutableListOf(), player)
                    wave = current
                    current.start()
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
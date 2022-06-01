package me.reidj.tower.listener

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.selection.button
import me.func.mod.selection.choicer
import me.func.mod.util.after
import me.func.mod.util.command
import me.func.mod.util.nbt
import me.reidj.tower.HUB
import me.reidj.tower.flying
import me.reidj.tower.game.Normal
import me.reidj.tower.game.Rating
import me.reidj.tower.item
import me.reidj.tower.upgrade.UpgradeInventory
import me.reidj.tower.upgrade.UpgradeType
import me.reidj.tower.user.Session
import me.reidj.tower.user.User
import me.reidj.tower.wave.Wave
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object InteractEvent : Listener {

    private const val MOVE_SPEED: Double = .01
    private const val CONST_TICKS_BEFORE_STRIKE = 20
    private const val TICKS_BEFORE_STRIKE = 40

    private val buttons = listOf(
        button {
            title = "Обычная"
            item = item {}.nbt("other", "villager")
            onClick { player, _, _ -> start(player).game = Normal() }
        },
        button {
            title = "Рейтинговая"
            item = item {}.nbt("other", "collection")
            onClick { player, _, _ -> start(player).game = Rating() }
        }
    )

    init {
        command("leave") { player, _ -> ITransferService.get().transfer(player.uniqueId, RealmId.of(HUB)) }

        command("play") { player, _ ->
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                if (inGame)
                    return@apply
                choicer {
                    title = "Tower Simulator"
                    description = "Выберите под-режим"
                    storage = buttons.toMutableList()
                }.open(player)
            }
        }
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

    private fun start(player: Player) = SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
        Anime.close(player)

        hideFromAll()

        session = Session(tower.upgrades)

        session?.upgrade?.values?.forEach { it.level = 1 }

        player.apply {
            inventory.clear()
            teleport(session?.arenaSpawn)
            inventory.setItem(4, UpgradeInventory.workshop)
            flying()
        }

        sword.giveSword(this)

        tower.health = tower.maxHealth
        tower.updateHealth()
        tower.update(
            this,
            UpgradeType.BULLET_DELAY,
            UpgradeType.DAMAGE,
            UpgradeType.HEALTH,
            UpgradeType.PROTECTION,
            UpgradeType.REGEN,
            UpgradeType.RADIUS
        )
        update(
            this,
            UpgradeType.CASH_BONUS_KILL,
            UpgradeType.CASH_BONUS_WAVE_PASS,
        )

        // Отправляем точки со спавнерами
        session?.generators?.forEach { ModTransfer(it.x, it.y, it.z).send("mobs:init", player) }

        Anime.counting321(player)

        // Начинаю волну
        inGame = true
        giveTokens(80)
        after(3 * 20) {
            val current = Wave(true, System.currentTimeMillis(), 1, mutableListOf(), player)
            wave = current
            current.start()

            // Игра началась
            ModTransfer(
                true,
                session!!.cubeLocation.x,
                session!!.cubeLocation.y,
                session!!.cubeLocation.z,
                MOVE_SPEED,
                TICKS_BEFORE_STRIKE,
                CONST_TICKS_BEFORE_STRIKE
            ).send("tower:update-state", player)
        }
    }
}
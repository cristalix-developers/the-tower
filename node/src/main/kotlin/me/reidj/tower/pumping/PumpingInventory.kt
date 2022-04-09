package me.reidj.tower.pumping

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.reidj.tower.user.User
import me.reidj.tower.util.MoneyFormat
import org.bukkit.Material.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.inventory.ClickableItem
import ru.cristalix.core.inventory.ControlledInventory
import ru.cristalix.core.inventory.InventoryContents
import ru.cristalix.core.inventory.InventoryProvider
import ru.kdev.simulatorapi.listener.SessionListener

/**
 * @project tower
 * @author Рейдж
 */
object PumpingInventory {

    private val backItem = item {
        type = CLAY_BALL
        text("§cНазад")
        nbt("other", "cancel")
    }

    val workshop = item {
        type = CLAY_BALL
        text("§bМастерская\n\n§7Улучшайте навыки, чтобы проходить\n§7волны было ещё легче!")
        nbt("other", "friend_add")
        nbt("click", "workshop")
    }

    private val menu = ControlledInventory.builder()
        .title("§bМастерская")
        .rows(4)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXXXXXX",
                    "XOXOXOXOX",
                    "XOXOXOXOX",
                    "XXXXQXXXX"
                )

                val user = SessionListener.simulator.getUser<User>(player.uniqueId)!!

                icon(user, contents, if (!user.inGame) user.upgradeTypes else user.session.upgrade)
                contents.add('Q', ClickableItem.of(backItem) { player.closeInventory() })
                contents.fillMask('X', ClickableItem.empty(item {
                    type = STAINED_GLASS_PANE
                    data = 7
                    text("&f")
                }))
            }
        }).build()

    init {
        // Команда для открытия меню
        B.regConsumerCommand({ player, _ -> menu.open(player) }, "workshop", "")
    }

    fun icon(user: User, contents: InventoryContents, upgradeTypes: MutableMap<UpgradeType, Upgrade>) {
        upgradeTypes.forEach { (pumpingType, pumping) ->
            val level = pumping.level
            val cost = pumpingType.startPrice + level
            val notInGame = !user.inGame
            contents.add('O', ClickableItem.of(item {
                type = CLAY_BALL
                text(
                    """§b${pumpingType.title}
                §7Цена ${MoneyFormat.toMoneyFormat(cost)}
                        
                §b$level §f➠ §b${level + 1} уровень §a▲▲▲
        
                §7${pumpingType.lore}
        
                §aНажмите чтобы улучшить
                """.trimIndent()
                )
                val pair = pumpingType.nbt.split(":")
                nbt(pair[0], pair[1])
            }) {
                if (if (notInGame) user.money >= cost else user.tokens >= cost) {
                    if (notInGame) user.giveMoney(-cost) else user.giveTokens(-cost)
                    pumping.level++
                    user.player!!.performCommand("workshop")

                    user.tower.updateHealth()
                    if (notInGame) user.update(user, pumpingType) else user.tower.update(user, pumpingType)
                } else {
                    user.player!!.closeInventory()
                    Anime.itemTitle(user.player!!, ItemStack(BARRIER), "Ошибка", "Недостаточно средств", 2.0)
                }
            })
        }
    }
}
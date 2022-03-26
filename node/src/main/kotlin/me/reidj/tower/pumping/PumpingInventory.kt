package me.reidj.tower.pumping

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.protocol.GlowColor
import me.reidj.tower.user.User
import me.reidj.tower.util.MoneyFormat
import org.bukkit.Material
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
        type = Material.CLAY_BALL
        text("§cНазад")
        nbt("other", "cancel")
    }

    private val menu = ControlledInventory.builder()
        .title("Улучшения атаки")
        .rows(6)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXXXXXX",
                    "XOXOXOXOX",
                    "XXXXXXXXX",
                    "XXXXXXXXX",
                    "XXXXXXXXX",
                    "XXXXQXXXX",
                )

                val user = SessionListener.simulator.getUser<User>(player.uniqueId)!!

                user.pumpingTypes.values.forEach { pumping ->
                    val cost = pumping.startPrice + pumping.level * 1000
                    contents.add('O', ClickableItem.of(item {
                        type = Material.CLAY_BALL
                        text(
                            """§b${pumping.title}
                        §7Цена ${MoneyFormat.toMoneyFormat(cost)}
                        
                        §b${pumping.level} §f➠ §b${pumping.level + 1} уровень §a▲▲▲
        
                        §7${pumping.lore}
        
                        §aНажмите чтобы улучшить
                        """.trimIndent()
                        )
                        val pair = pumping.nbt.split(":")
                        nbt(pair[0], pair[1])
                    }) {
                        if (user.money >= cost) {
                            user.money -= cost
                            pumping.upgradable += pumping.step
                            pumping.level++
                            Glow.animate(player, .5, GlowColor.GREEN)
                        } else {
                            player.closeInventory()
                            Anime.itemTitle(player, ItemStack(Material.BARRIER), "Ошибка", "Недостаточно средств", 2.0)
                        }
                    })
                }
                contents.add('Q', ClickableItem.of(backItem) { player.closeInventory() })
                contents.fillMask('X', ClickableItem.empty(item {
                    type = Material.STAINED_GLASS_PANE
                    data = 7
                    text("&f")
                }))
            }
        }).build()

    init {
        // Команда для открытия меню
        B.regConsumerCommand({ player, _ -> menu.open(player) }, "settings", "")
    }
}
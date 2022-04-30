package me.reidj.tower.upgrade

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.reidj.tower.*
import me.reidj.tower.content.MainGui.backItem
import me.reidj.tower.user.User
import me.reidj.tower.util.MoneyFormat
import org.bukkit.Material.BARRIER
import org.bukkit.Material.STAINED_GLASS_PANE
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
object UpgradeInventory {

    val workshop = item {
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

                icon(
                    user, contents, if (!user.inGame) {
                        user.upgradeTypes
                        user.tower.upgrades
                    } else {
                        user.session!!.upgrade
                    }
                )

                contents.add('Q', ClickableItem.of(backItem) { player.closeInventory() })
                contents.fillMask('X', ClickableItem.empty(item(STAINED_GLASS_PANE) {
                    data(7)
                    text("&f")
                }))
            }
        }).build()

    init {

        // Команда для открытия меню
        command("workshop") { player, _ ->
            menu.open(player)
            /*SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                icon(
                    this, if (!inGame) {
                        upgradeTypes
                        tower.upgrades
                    } else {
                        session!!.upgrade
                    }
                )
            }*/

        }
    }

    fun icon(user: User, contents: InventoryContents, vararg upgradeTypes: MutableMap<UpgradeType, Upgrade>) {
        upgradeTypes.forEachIndexed { index, mutableMap ->
            mutableMap.forEach { (upgradeType, upgrade) ->
                val level = upgrade.level
                val cost = upgradeType.price + level
                val notInGame = !user.inGame
                ModTransfer(index + 2, upgradeType.title, cost, level, upgradeType.lore).send(
                    "upgradegui:init",
                    user.player
                )
                contents.add('O', ClickableItem.of(item {
                    text(
                        """§b${upgradeType.title}
            §7Цена ${MoneyFormat.toMoneyFormat(cost)}

            §b$level §f➠ §b${level + 1} уровень §a▲▲▲

            §7${upgradeType.lore}

            §aНажмите чтобы улучшить
            """.trimIndent()
                    )
                    val pair = upgradeType.nbt.split(":")
                    nbt(pair[0], pair[1])
                }) {
                    if (if (notInGame) user.money >= cost else user.tokens >= cost) {
                        if (notInGame) user.giveMoney(-cost) else user.giveTokens(-cost)
                        upgrade.level++
                        user.player!!.performCommand("workshop")
                        user.tower.updateHealth()
                        if (notInGame)
                            user.update(user)
                        else
                            user.session!!.update(
                                user,
                                UpgradeType.BULLET_DELAY,
                                UpgradeType.DAMAGE,
                                UpgradeType.HEALTH,
                                UpgradeType.PROTECTION,
                                UpgradeType.REGEN,
                                UpgradeType.RADIUS
                            )
                    } else {
                        user.player!!.closeInventory()
                        Anime.itemTitle(user.player!!, ItemStack(BARRIER), "Ошибка", "Недостаточно средств", 2.0)
                    }
                })
            }
        }
    }
}
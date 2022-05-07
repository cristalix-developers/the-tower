package me.reidj.tower.upgrade

import me.func.mod.conversation.ModTransfer
import me.reidj.tower.command
import me.reidj.tower.item
import me.reidj.tower.nbt
import me.reidj.tower.text
import me.reidj.tower.user.User
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

    init {
        // Команда для открытия меню
        command("workshop") { player, _ ->
            //ModTransfer().send("upgradegui:init", player)
            //menu.open(player)
            SessionListener.simulator.getUser<User>(player.uniqueId)!!.apply {
                icon(
                    this, if (!inGame) {
                        upgradeTypes
                        tower.upgrades
                    } else {
                        session!!.upgrade
                    }
                )
            }

        }
    }

    private fun icon(user: User, vararg upgradeTypes: MutableMap<UpgradeType, Upgrade>) {
        upgradeTypes.forEachIndexed { index, mutableMap ->
            mutableMap.forEach { (upgradeType, upgrade) ->
                val level = upgrade.level
                val cost = upgradeType.price + level
                val notInGame = !user.inGame
                ModTransfer(
                    index + 7,
                    upgradeType.title,
                    cost,
                    level,
                    upgradeType.lore
                ).send("upgradegui:init", user.player)
                /*contents.add('O', ClickableItem.of(item {
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
                })*/
            }
        }
    }
}
package me.reidj.tower.upgrade

import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.data.Category
import me.reidj.tower.data.Improvement
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.sword.SwordType
import me.reidj.tower.user.User
import me.reidj.tower.util.Formatter
import me.reidj.tower.util.PATH
import me.reidj.tower.util.error

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class UpgradeMenu {

    init {
        command("workshop") { player, args ->
            val user = app.getUser(player) ?: return@command
            open(
                user,
                if (!user.inGame) user.stat.userImprovementType else user.session!!.towerImprovement,
                Category.valueOf(args[0])
            )
        }
    }

    private val menu = selection {
        title = "Улучшения"
        hint = "Купить"
    }

    private fun open(user: User, type: MutableMap<ImprovementType, Improvement>, category: Category) {
        val notInGame = !user.inGame
        val stat = user.stat
        menu.money =
            if (notInGame) "Монет ${Formatter.toFormat(stat.money)}" else "Токенов ${Formatter.toFormat(user.tokens)}"
        menu.rows = 3
        menu.columns = 3
        menu.vault = if (notInGame) "${PATH}coin.png" else "${PATH}token.png"
        menu.storage = type.filter { it.key.category == category }.map { (key, value) ->
            val level = value.level
            val cost = key.price + level
            button {
                texture = key.texture
                price = cost.toLong()
                title = "§3${value.level} LVL"
                description = key.description
                onClick { player, _, _ ->
                    if (if (notInGame) stat.money >= cost else user.tokens >= cost) {
                        if (notInGame) user.giveMoney(-cost) else user.giveToken(-cost)
                        value.level++
                        user.tower!!.updateHealth()
                        player.performCommand("workshop ${category.name}")
                        if (notInGame) {
                            user.update(user)
                            SwordType.valueOf(stat.sword).update(user)
                        } else {
                            user.session!!.run {
                                updateHealth(user)
                                updateBulletDelay(user)
                                updateDamage(user)
                                updateProtection(user)
                            }
                            user.session!!.update(user, ImprovementType.RADIUS)
                        }
                    } else {
                        player.error("Недостаточно средств")
                    }
                }
            }
        }.toMutableList()
        menu.open(user.player)
    }
}
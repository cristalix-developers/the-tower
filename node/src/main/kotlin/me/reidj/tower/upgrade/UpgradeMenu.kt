package me.reidj.tower.upgrade

import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.reidj.tower.app
import me.reidj.tower.data.Improvement
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.sword.SwordType
import me.reidj.tower.user.User
import me.reidj.tower.util.Formatter
import me.reidj.tower.util.error

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class UpgradeMenu {

    init {
        command("workshop") { player, _ ->
            val user = app.getUser(player) ?: return@command
            open(user, if (!user.inGame) user.stat.userImprovementType else user.session!!.towerImprovement)
        }
    }

    private val menu = selection {
        title = "Улучшения"
        hint = "Купить"
    }

    private fun open(user: User, type: MutableMap<ImprovementType, Improvement>) {
        val notInGame = !user.inGame
        val stat = user.stat
        menu.money =
            if (notInGame) "Монет ${Formatter.toFormat(stat.money)}" else "Токенов ${Formatter.toFormat(user.tokens)}"
        menu.rows = 3
        menu.columns = 3
        menu.vault = if (notInGame) "coin" else "ruby"
        menu.storage = type.map { (key, value) ->
            val level = value.level
            val cost = key.price + level
            button {
                texture = key.texture
                price = cost.toLong()
                title = "§3${value.level} LVL"
                description = key.description
                onClick { player, _, _ ->
                    if (if (notInGame) stat.money >= cost else user.tokens >= cost) {
                        if (notInGame) user.giveMoney(-cost) else user.giveTokens(-cost)
                        value.level++
                        user.tower!!.updateHealth()
                        player.performCommand("workshop")
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
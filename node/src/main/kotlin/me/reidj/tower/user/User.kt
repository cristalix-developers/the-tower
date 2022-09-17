package me.reidj.tower.user

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.ui.Glow
import me.func.protocol.data.color.GlowColor
import me.reidj.tower.app
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.Pumping
import me.reidj.tower.data.ResearchType
import me.reidj.tower.data.Stat
import me.reidj.tower.game.wave.Wave
import me.reidj.tower.upgrade.Upgradable
import me.reidj.tower.util.LevelSystem
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class User(stat: Stat) : Upgradable {
    val stat: Stat

    lateinit var player: Player

    var session: Session? = null
    var tower: Tower? = null
    var wave: Wave? = null

    var tokens = 0.0
    var health = 5.0

    var inGame = false
    var isArmLock = false
    var isTournament = false

    init {
        this.stat = stat
    }

    fun getLevel() = LevelSystem.getLevel(stat.experience)

    fun hideFromAll() {
        Bukkit.getOnlinePlayers().forEach { current ->
            player.hidePlayer(app, current)
            current.hidePlayer(app, player)
        }
    }

    fun showToAll() {
        Bukkit.getOnlinePlayers()
            .mapNotNull { app.getUser(it) }
            .filter { !it.inGame }
            .forEach {
                it.player.showPlayer(app, player)
                player.showPlayer(app, player)
            }
    }

    fun giveMoney(money: Double) {
        stat.money += money
        ModTransfer(stat.money).send("tower:money", player)
    }

    fun giveRebirth(rebirth: Int) {
        stat.rebirth += rebirth
    }

    fun giveTokens(tokens: Double) {
        this.tokens += tokens
        ModTransfer(this.tokens).send("tower:tokens", player)
    }

    fun giveGem(gem: Int) {
        stat.gem += gem
        ModTransfer(stat.gem).send("tower:gem", player)
    }

    fun giveExperience(exp: Int) {
        val prevLevel = getLevel()
        stat.experience += exp
        ModTransfer(
            stat.experience,
            LevelSystem.getRequiredExperience(LevelSystem.getLevel(stat.experience))
        ).send("tower:exp", player)
        if (getLevel() > prevLevel) {
            Anime.alert(
                player,
                "§lПоздравляем!",
                "Ваш уровень был повышен!\n§7$prevLevel §f ➠ §l${getLevel()}"
            )
            Glow.animate(player, .5, GlowColor.BLUE)
        }
    }

    override fun update(user: User, vararg pumping: Pumping) {
        pumping.filterIsInstance<ImprovementType>()
            .forEach {
                ModTransfer(stat.userImprovementType[ImprovementType.valueOf(it.name)]!!.getValue()).send(
                    "user:${it.name.lowercase()}",
                    user.player
                )
            }
        pumping.filterIsInstance<ResearchType>()
            .forEach {
                ModTransfer(stat.researchType[ResearchType.valueOf(it.name)]!!.getValue()).send(
                    "user:${it.name.lowercase()}",
                    user.player
                )
            }
    }
}
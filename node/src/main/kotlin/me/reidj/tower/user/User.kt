package me.reidj.tower.user

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.reactive.ReactiveProgress
import me.func.mod.ui.Glow
import me.func.mod.ui.booster.Booster
import me.func.mod.ui.booster.Boosters
import me.func.protocol.data.color.GlowColor
import me.func.protocol.math.Position
import me.reidj.tower.app
import me.reidj.tower.booster.BoosterType
import me.reidj.tower.clientSocket
import me.reidj.tower.data.ImprovementType
import me.reidj.tower.data.Pumping
import me.reidj.tower.data.ResearchType
import me.reidj.tower.data.Stat
import me.reidj.tower.donate.StartingKit
import me.reidj.tower.game.wave.Wave
import me.reidj.tower.protocol.SaveUserPackage
import me.reidj.tower.upgrade.Upgradable
import me.reidj.tower.util.LevelSystem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.cristalix.core.formatting.Formatting
import java.util.function.Supplier

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

    val activeProgress = hashMapOf<ResearchType, ReactiveProgress>()

    val progress = Supplier {
        ReactiveProgress.builder()
            .position(Position.BOTTOM)
            .hideOnTab(false)
            .color(GlowColor.BLUE)
    }

    init {
        this.stat = stat
    }

    fun getLevel() = LevelSystem.getLevel(stat.experience)

    fun requiredExperience() = stat.experience - LevelSystem.getRequiredExperience(getLevel() - 1)

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

    fun giveMoneyWithBooster(money: Double) {
        giveMoney(money * app.playerDataManager.calcMultiplier(stat.uuid, BoosterType.MONEY))
    }

    fun giveMoney(money: Double) {
        stat.money += money * if (StartingKit.STARTER_KIT.name in stat.donates) 2.0 else if (StartingKit.EPIC_KIT.name in stat.donates) 4.0 else 1.0
        ModTransfer(stat.money).send("tower:money", player)
    }

    fun giveRebirth(rebirth: Int) {
        stat.rebirth += rebirth
    }

    fun giveTokenWithBooster(tokens: Double) {
        giveToken(tokens * app.playerDataManager.calcMultiplier(stat.uuid, BoosterType.TOKEN))
        ModTransfer(this.tokens).send("tower:tokens", player)
    }

    fun giveToken(tokens: Double) {
        this.tokens += tokens
        ModTransfer(this.tokens).send("tower:tokens", player)
    }

    fun giveGemWithBooster(gem: Int) {
        giveGem(gem * app.playerDataManager.calcMultiplier(stat.uuid, BoosterType.GEM).toInt())
    }

    fun giveGem(gem: Int) {
        stat.gem += gem
        ModTransfer(stat.gem).send("tower:gem", player)
    }

    fun giveExperienceWithBooster(exp: Double) {
        giveExperience(exp * app.playerDataManager.calcMultiplier(stat.uuid, BoosterType.EXP))
    }

    fun giveExperience(exp: Double) {
        val prevLevel = getLevel()
        stat.experience += exp
        ModTransfer(
            requiredExperience(),
            LevelSystem.getRequiredExperience(getLevel()) - LevelSystem.getRequiredExperience(getLevel() - 1)
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

    fun calcMultiplier(type: BoosterType): Double {
        stat.localBoosters.removeIf {
            val title = it.type.title
            Boosters.send(player, Booster("Бустер $title", it.multiplier))
            player.sendMessage(Formatting.fine("Локальный §bбустер $title §fзакончился!"))
            it.hadExpire()
        }
        var sum = 1.0
        for (booster in stat.localBoosters) {
            if (booster.type == type) {
                sum += booster.multiplier - 1
            }
        }
        clientSocket.write(SaveUserPackage(stat.uuid, stat))
        return sum
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
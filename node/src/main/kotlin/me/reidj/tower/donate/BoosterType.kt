package me.reidj.tower.donate

import me.func.mod.Anime
import me.reidj.tower.app
import me.reidj.tower.booster.BoosterInfo
import me.reidj.tower.clientSocket
import me.reidj.tower.protocol.SaveGlobalBoosterPackage
import me.reidj.tower.user.User
import me.reidj.tower.util.PATH
import org.bukkit.Bukkit

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class BoosterType(
    private val title: String,
    private val description: String,
    private val price: Long,
    private val boosterInfo: (User) -> BoosterInfo
) : Donate {
    GLOBAL_EXP_BOOST(
        "Глобальный бустер Опыта §bх2",
        "Глобальный бустер на §b1 час§f, Вы получаете в два раза больше Опыта!",
        99,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.EXP, true) }
    ),
    LOCAL_EXP_BOOST(
        "Локальный бустер Опыта §bх2",
        "Локальный бустер на §b1 час§f, Вы получаете в два раза больше Опыта!",
        59,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.EXP, false) }
    ),
    GLOBAL_GEM_BOOST(
        "Глобальный бустер Самоцветов §bх2",
        "Глобальный бустер на §b1 час§f, Вы получаете в два раза больше Самоцветов!",
        109,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.GEM, true) }
    ),
    LOCAL_GEM_BOOST(
        "Локальный бустер Самоцветов §bх2",
        "Локальный бустер на §b1 час§f, Вы получаете в два раза больше Самоцветов!",
        69,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.GEM, false) }
    ),
    GLOBAL_TOKEN_BOOST(
        "Глобальный бустер Токенов §bх2",
        "Глобальный бустер на §b1 час§f, Вы получаете в два раза больше Токенов!",
        99,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.TOKEN, true) }
    ),
    LOCAL_TOKEN_BOOST(
        "Локальный бустер Токенов §bх2",
        "Локальный бустер на §b1 час§f, Вы получаете в два раза больше Токенов!",
        59,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.TOKEN, false) }
    ),
    GLOBAL_MONEY_BOOST(
        "Глобальный бустер Монет §bх2",
        "Глобальный бустер на §b1 час§f, Вы получаете в два раза больше Монет!",
        109,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.MONEY, true) }
    ),
    LOCAL_MONEY_BOOST(
        "Локальный бустер Монет §bх2",
        "Локальный бустер на §b1 час§f, Вы получаете в два раза больше Монет!",
        69,
        { BoosterInfo.defaultInstance(it.stat.uuid, it.player.displayName, me.reidj.tower.booster.BoosterType.MONEY, false) }
    ),
    ;

    override fun getTitle() = title

    override fun getDescription() = description

    override fun getTexture() = "$PATH${name.lowercase()}"

    override fun getObjectName() = name

    override fun getPrice() = price

    override fun give(user: User) {
        val booster = boosterInfo(user)
        val title = booster.type.title
        if (booster.isGlobal) {
            app.playerDataManager.globalBoosters.add(booster)
            Bukkit.getOnlinePlayers().forEach {
                val message = "Активирован глобальный бустер §b$title§f! Поблагодарить §b/thx"
                Anime.topMessage(it, message)
                it.sendMessage(message)
                app.playerDataManager.sendBoosters(it, booster)
            }
        } else {
            user.stat.localBoosters.add(booster)
            val message = "Активирован локальный бустер §b$title§f!"
            Anime.topMessage(user.player, message)
            user.player.sendMessage(message)
            app.playerDataManager.sendBoosters(user.player, booster)
        }
        clientSocket.write(SaveGlobalBoosterPackage(booster))
    }
}
package me.reidj.tower.donate

import me.reidj.tower.user.User
import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class GemKit(
    private val title: String,
    private val price: Long,
    private val reward: Int
): Donate {
    HANDFUL_OF_GEM("Горсть Самоцветов", 29, 119),
    BAG_OF_GEM("Мешок с Самоцветами", 39, 159),
    CHEST_WITH_GEM("Сундук с Самоцветами", 59, 199),
    TREASURY("Сокровищница с Самоцветами", 79, 299),
    SAFE("Сейф с Самоцветами", 109, 499),
    ;

    override fun getTitle() = title

    override fun getDescription() = "Вы получите $reward Самоцветов"

    override fun getTexture() = "$PATH${name.lowercase()}.png"

    override fun getObjectName() = name

    override fun getPrice() = price

    override fun isSave() = true

    override fun give(user: User) {
        user.giveGem(reward)
    }
}
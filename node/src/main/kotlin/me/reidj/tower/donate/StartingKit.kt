package me.reidj.tower.donate

import me.reidj.tower.data.Stat
import me.reidj.tower.user.User
import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class StartingKit(
    private val title: String,
    private val description: String,
    private val price: Long,
    private val reward: Int,
) : Donate {
    STARTER_KIT("Стартовый набор", "Постоянное увеличение всех заработанных монет в 2 раза и 150 Самоцветов", 249, 150),
    EPIC_KIT("Эпический набор", "Постоянное увеличение всех заработанных монет в 4 раза и 750 Самоцветов", 399, 750),
    ;

    override fun getTitle() = title

    override fun getDescription() = description

    override fun getTexture() = "$PATH${name.lowercase()}.png"

    override fun getObjectName() = name

    override fun getPrice() = price

    override fun give(user: User) {
        user.giveGem(reward)
    }

    override fun getCurrent(stat: Stat) = false

    override fun setCurrent(stat: Stat) = Unit
}
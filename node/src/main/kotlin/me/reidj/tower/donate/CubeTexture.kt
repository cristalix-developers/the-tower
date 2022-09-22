package me.reidj.tower.donate

import me.reidj.tower.user.User
import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class CubeTexture(
    private val title: String,
    private val price: Long
) : Donate {
    CRYSTAL("", 0),
    MIGRATION("", 59),
    EMERALD("", 59),
    RUBY("", 59),
    STRUCTURE("", 59),
    ;

    override fun getTitle() = title

    override fun getDescription() = ""

    override fun getTexture() = "$PATH${name.lowercase()}.png"

    override fun getObjectName() = name

    override fun getPrice() = price

    override fun give(user: User) {

    }
}
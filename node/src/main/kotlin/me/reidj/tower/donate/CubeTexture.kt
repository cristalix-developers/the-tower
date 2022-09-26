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
    CRYSTAL("Кристальный", 0),
    MIGRATION("Демонический", 59),
    EMERALD("Изумрудный", 59),
    RUBY("Рубиновый", 59),
    STRUCTURE("Астральный", 59),
    ;

    override fun getTitle() = "$title куб"

    override fun getDescription() = ""

    override fun getTexture() = "$PATH${name.lowercase()}.png"

    override fun getObjectName() = name

    override fun getPrice() = price

    override fun isSave() = true

    override fun give(user: User) {

    }
}
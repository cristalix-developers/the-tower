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
    CROSS("Крестовый куб", 0),
    CARAMEL("Карамельный куб", 39),
    WISH("Куб желаний", 39),
    ICE_HEART("Куб ледяного сердца", 39),
    RUBY("Рубиновый куб", 39),
    TIME_LOOP("Куб петля времени", 59),
    MAGIC("Магический куб", 59),
    GEM_CUBE("Куб Самоцветов", 59),
    DEMON("Демонический куб", 59),
    ;

    override fun getTitle() = title

    override fun getDescription() = ""

    override fun getTexture() = "$PATH${name.lowercase()}.png"

    override fun getObjectName() = name

    override fun getPrice() = price

    override fun isSave() = true

    override fun give(user: User) {

    }
}
package me.reidj.tower.sound

import me.func.sound.Category
import me.func.sound.Sound
import me.reidj.tower.util.STORAGE
import org.bukkit.entity.Player

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class SoundType {
    GEM_DROP,
    BUY,
    CLICK_MENU,
    ;

    fun send(player: Player) = Sound("${STORAGE}${name.lowercase()}.ogg")
        .category(Category.MUSIC)
        .pitch(1.0f)
        .volume(1.5f)
        .repeating(false)
        .send(player)
}
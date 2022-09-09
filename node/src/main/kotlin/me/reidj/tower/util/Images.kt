package me.reidj.tower.util

/**
 * @project : tower
 * @author : Рейдж
 **/
enum class Images {
    ENERGY,
    HEALTH_BAR,
    TOURNAMENT,
    BRONZE,
    GOLD,
    SILVER,
    PLATINUM,
    DIAMOND,
    ;

    fun path() = "https://storage.c7x.ru/reidj/tower/${name.lowercase()}.png"
}
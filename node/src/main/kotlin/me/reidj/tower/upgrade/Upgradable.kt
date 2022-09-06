package me.reidj.tower.upgrade

import me.reidj.tower.data.Pumping
import me.reidj.tower.user.User

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

@FunctionalInterface
interface Upgradable {

    fun update(user: User, vararg pumping: Pumping)
}
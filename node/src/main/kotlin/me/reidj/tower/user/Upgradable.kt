package me.reidj.tower.user

@FunctionalInterface
interface Upgradable {

    fun update(user: User, vararg type: Upgrade)

}
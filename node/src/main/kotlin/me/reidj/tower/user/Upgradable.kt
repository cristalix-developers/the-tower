package me.reidj.tower.user

import me.reidj.tower.pumping.UpgradeType

@FunctionalInterface
interface Upgradable {

    fun update(user: User, vararg type: UpgradeType)

}
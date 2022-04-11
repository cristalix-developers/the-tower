package me.reidj.tower.user

import me.reidj.tower.upgrade.UpgradeType

@FunctionalInterface
interface Upgradable {

    fun update(user: User, vararg type: UpgradeType)

}
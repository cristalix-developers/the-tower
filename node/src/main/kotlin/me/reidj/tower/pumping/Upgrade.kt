package me.reidj.tower.pumping

/**
 * @project tower
 * @author Рейдж
 */
class Upgrade(private val upgradeType: UpgradeType, var level: Int) {

    fun getValue(): Double = upgradeType.value + upgradeType.step * level
}
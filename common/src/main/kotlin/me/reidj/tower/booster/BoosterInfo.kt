package me.reidj.tower.booster

import java.util.*

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
data class BoosterInfo(
    val uuid: UUID,
    val owner: UUID,
    val ownerName: String,
    val type: BoosterType,
    val until: Double,
    val time: Double,
    val multiplier: Double,
    val isGlobal: Boolean,
) {
    companion object {
        fun defaultInstance(user: UUID, userName: String, type: BoosterType, isGlobal: Boolean, time: Long = 36) =
            BoosterInfo(
                UUID.randomUUID(),
                user,
                userName,
                type,
                (System.currentTimeMillis() + time).toDouble(),
                time.toDouble(),
                type.multiplier,
                isGlobal
            )
    }

    fun hadExpire() = System.currentTimeMillis() > until
}

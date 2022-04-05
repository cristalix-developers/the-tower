import dev.xdark.clientapi.entity.EntityLivingBase

/**
 * @project tower
 * @author Рейдж
 */

fun EntityLivingBase.updateNameHealth() = apply { customNameTag = "§4${health.toInt()} ❤" }
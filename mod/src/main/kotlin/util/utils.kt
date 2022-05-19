import dev.xdark.clientapi.entity.EntityLivingBase
import util.Formatter

/**
 * @project tower
 * @author Рейдж
 */

fun EntityLivingBase.updateNameHealth() = apply { customNameTag = "§4${Formatter.toFormat(health.toDouble())} ❤" }
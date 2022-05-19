import dev.xdark.clientapi.entity.EntityLivingBase
import tower.TowerManager
import util.Formatter

/**
 * @project tower
 * @author Рейдж
 */

fun EntityLivingBase.updateNameHealth() = apply { customNameTag = "§4${Formatter.toFormat(health.toDouble())} ❤" }

fun EntityLivingBase.updateHealth() = apply {
    health -= TowerManager.damage.toFloat()
    updateNameHealth()
}
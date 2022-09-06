import dev.xdark.clientapi.entity.EntityLivingBase
import tower.TowerManager
import util.Formatter
import kotlin.math.abs

/**
 * @project tower
 * @author Рейдж
 */

fun EntityLivingBase.updateNameHealth() = apply { customNameTag = "§4${Formatter.toFormat(health.toDouble())} ❤" }

fun EntityLivingBase.updateHealth() = apply {
    health -= TowerManager.damage.toFloat()
    updateNameHealth()
}

fun Double.plural(one: String, two: String, five: String): String {
    val n = abs(this) % 100
    val n1 = n % 10
    if (n in 11.0..20.0)
        return two
    else if (n1 in 2.0..4.0)
        return five
    else if (n1 == 1.0)
        return one
    return five
}
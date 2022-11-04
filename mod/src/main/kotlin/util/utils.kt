
import dev.xdark.clientapi.entity.EntityLivingBase
import io.netty.buffer.Unpooled
import ru.cristalix.uiengine.UIEngine
import tower.TowerManager
import util.Formatter

/**
 * @project tower
 * @author Рейдж
 */

fun EntityLivingBase.updateNameHealth() = apply { customNameTag = "§4${Formatter.toFormat(health.toDouble())} ❤" }

fun EntityLivingBase.updateHealth(damage: Double) = apply {
    health -= damage.toFloat()
    updateNameHealth()
}

fun EntityLivingBase.hitTower() {
    UIEngine.clientApi.clientConnection()
        .sendPayload("tower:hittower", Unpooled.copiedBuffer(uniqueID.toString(), Charsets.UTF_8))
}

fun MutableList<TowerManager.Bullet>.removeAll() {
    with(this.iterator()) {
        forEach { ammo ->
            UIEngine.worldContexts.remove(ammo.sphere)
            remove()
        }
    }
}

fun screenCheck(): Boolean {
    val currentScreen = UIEngine.clientApi.minecraft().currentScreen()
    return currentScreen == null || currentScreen::class.java.simpleName == "aV"
}
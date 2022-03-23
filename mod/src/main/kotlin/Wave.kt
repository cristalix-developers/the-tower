import ru.cristalix.clientapi.JavaMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import kotlin.math.roundToInt

class Wave(startTime: Long, level: Int, xyz: ArrayList<V3>, dest: V3) {

    private val amount = (level + level / 1.5).roundToInt() // 10 волна - 17 мобов, 40 волна - 67 и тд
    private val locs = xyz
    private val towerLoc = dest

    var time = startTime
    var waveLevel = level

    fun start() {
        waveLevel++
        JavaMod.clientApi.chat().printChatMessage("Началась $waveLevel волна")
        val amt = amount / locs.size - amount % locs.size
        var extra = amount % locs.size
        locs.forEach {
            if (extra > 0) {
                drawMob(it, amt + extra)
                extra = 0
            } else {
                drawMob(it, amt)
            }
        }
    }

    private fun drawMob(loc: V3, amt: Int) {
        mod.mobs.add(Mob(loc, 3.0).create())
        //JavaMod.clientApi.chat().printChatMessage("create mob")
        //TODO: сделать эти треугольнички
        /*repeat(amt) {
            Mob(
                this,
                MobType.values().toList()[java.util.Random().nextInt(MobType.values().toList().size)],
                2/*на самом деле здесь должно быть сам треугольник*/,
                loc,
                towerLoc
            )
        }*/
    }

    fun end() {
        // Если прошло 20 секунд после начала начинать следующую волну через 5 секунд
        JavaMod.clientApi.chat().printChatMessage("Волна закончилась")
        time = System.currentTimeMillis()
        UIEngine.schedule(5) { start() }
    }
}
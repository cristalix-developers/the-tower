import ru.cristalix.uiengine.utility.V3
import kotlin.math.roundToInt

class Wave(level: Int, xyz: ArrayList<V3>, dest: V3) {

    private val amount = (level + level / 1.5).roundToInt() // 10 волна - 17 мобов, 40 волна - 67 и тд
    private val locs = xyz
    val waveLevel = level
    private val towerLoc = dest

    fun start(){
        val amt = amount / locs.size - amount%locs.size
        var extra = amount%locs.size
        locs.forEach { t ->
            if(extra>0) {
                drawMob(t, amt+extra)
                extra = 0
            }else{
                drawMob(t, amt)
            }
        }
    }

    fun drawMob(loc: V3, amt: Int){
        //TODO: сделать эти треугольнички
        Mob(this, 2/*на самом деле здесь должно быть сам треугольник*/,loc, towerLoc)
    }



    fun end(){
        //TODO: если все мобы повержены, запускать следующую волну через 20 секунд
    }

}
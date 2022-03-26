import kotlin.math.sqrt

/**
 * @project tower
 * @author Рейдж
 */
class Vector constructor(var x: Double, var y: Double, var z: Double) {

    fun multiply(m: Double): Vector {
        x *= m
        y *= m
        z *= m
        return this
    }

    private fun square(num: Double): Double {
        return num * num
    }

    private fun length(): Double {
        return sqrt(square(x) + square(y) + square(z))
    }

    fun normalize(): Vector {
        val length: Double = this.length()
        x /= length
        y /= length
        z /= length
        return this
    }
}
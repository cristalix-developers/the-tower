import kotlin.math.sqrt

/**
 * @project tower
 * @author Рейдж
 */
class Vector constructor(var x: Double, var y: Double, var z: Double) {

    fun subtract(vec: Vector): Vector {
        x -= vec.x
        y -= vec.y
        z -= vec.z
        return this
    }

    fun multiply(m: Double): Vector {
        x *= m
        y *= m
        z *= m
        return this
    }

    fun square(num: Double): Double {
        return num * num
    }

    fun length(): Double {
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
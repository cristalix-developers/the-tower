package util

import java.text.DecimalFormat

object Formatter {

    private val FORMAT = DecimalFormat("##.#")

    fun toFormat(health: Double): String = FORMAT.format(health)
}
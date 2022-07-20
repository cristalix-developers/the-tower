package util

import java.text.DecimalFormat

object Formatter {

    private val FORMAT = DecimalFormat("##.#")

    fun toFormat(double: Double): String = FORMAT.format(double)
}
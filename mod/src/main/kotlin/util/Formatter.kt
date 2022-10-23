package util

import java.text.DecimalFormat

object Formatter {

    private val moneyFormat = DecimalFormat("#,###,###,##0")

    private val doubleFormat = DecimalFormat("#,###.##")

    fun toMoneyFormat(double: Double): String = moneyFormat.format(double)

    fun toFormat(double: Double): String = doubleFormat.format(double)
}
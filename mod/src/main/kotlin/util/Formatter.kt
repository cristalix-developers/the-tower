package util

import java.text.DecimalFormat

object Formatter {

    private val moneyFormat = DecimalFormat("#,###,###,##0")

    private val healthFormat = DecimalFormat("#,###.##")

    fun toMoneyFormat(double: Double): String = moneyFormat.format(double)

    fun toHealthFormat(double: Double): String = healthFormat.format(double)
}
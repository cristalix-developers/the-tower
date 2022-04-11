package me.reidj.tower.util

import java.text.DecimalFormat

/**
 * @project tower
 * @author Рейдж
 */
object MoneyFormat {

    fun toMoneyFormat(money: Int): String? = DecimalFormat("###,###,###,###,###,###.##$").format(money)
}
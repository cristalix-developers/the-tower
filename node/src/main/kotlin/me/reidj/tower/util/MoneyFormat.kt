package me.reidj.tower.util

import java.text.DecimalFormat

/**
 * @project tower
 * @author Рейдж
 */
object MoneyFormat {

    private val MONEY_FORMAT = DecimalFormat("###,###,###,###,###,###.##$")

    fun toMoneyFormat(money: Int): String? {
        return MONEY_FORMAT.format(money)
    }
}
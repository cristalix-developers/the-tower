package me.reidj.tower.ticker

interface Ticked {

    suspend fun tick(vararg args: Int)
}
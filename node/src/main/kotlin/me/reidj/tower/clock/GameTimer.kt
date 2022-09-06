package me.reidj.tower.clock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/

@FunctionalInterface
interface ClockInject {

    fun run(tick: Int)
}

class GameTimer(private val injects: List<ClockInject>) : () -> Unit {

    private var tick = 0
    private val maxTick = 1000000000

    private val scope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()

    override fun invoke() {
        if (mutex.isLocked) return
        scope.launch {
            mutex.withLock {
                tick++

                if (tick > maxTick)
                    tick = 0

                injects.forEach { it.run(tick) }
            }
        }
    }
}
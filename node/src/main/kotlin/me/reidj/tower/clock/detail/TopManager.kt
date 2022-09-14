package me.reidj.tower.clock.detail

import com.google.common.collect.Maps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import me.reidj.tower.app
import me.reidj.tower.clientSocket
import me.reidj.tower.clock.ClockInject
import me.reidj.tower.protocol.TopPackage
import me.reidj.tower.top.TopEntry
import org.bukkit.Location
import ru.cristalix.boards.bukkitapi.Board
import ru.cristalix.boards.bukkitapi.Boards
import ru.cristalix.core.GlobalSerializers
import java.text.DecimalFormat
import java.util.*

private const val DATA_COUNT = 10
private val TOP_DATA_FORMAT = DecimalFormat("###,###,###")

class TopManager : ClockInject {
    private val tops = Maps.newConcurrentMap<String, List<TopEntry<String, String>>>()
    private val boards = Maps.newConcurrentMap<String, Board>()

    init {
        // Создание топов
        app.worldMeta.getLabels("top").forEach {
            val pair = it.tag.split(" ")
            boards[pair[0]] = newBoard("Топ по Перерождениям", "Перерождений", it.apply {
                x += 0.5
                y += 4.5
                yaw = pair[1].toFloat()
                pitch = 0f
            })
        }
    }

    private fun newBoard(title: String, fieldName: String, location: Location) = Boards.newBoard().also {
        it.addColumn("#", 20.0)
        it.addColumn("Игрок", 110.0)
        it.addColumn(fieldName, 80.0)
        it.title = title
        it.location = location
    }.also(Boards::addBoard)

    private fun updateData() {
        CoroutineScope(Dispatchers.IO).launch {
            for (field in boards.keys) {
                val topPackageResponse = clientSocket.writeAndAwaitResponse<TopPackage>(
                    TopPackage(
                        field,
                        DATA_COUNT,
                        false
                    )
                ).await()
                tops[field] = topPackageResponse.entries.map {
                    TopEntry(
                        if (it.displayName == null) "ERROR" else it.displayName!!,
                        TOP_DATA_FORMAT.format(it.value)
                    )
                }
            }
        }
    }

    override fun run(tick: Int) {
        if (tick % 60 * 20 != 0)
            return
        kotlin.runCatching {
            updateData()
            val data = GlobalSerializers.toJson(tops)
            if ("{}" == data || data == null) return@runCatching
            boards.forEach { (field, top) ->
                top.clearContent()
                var counter = 0
                if (tops[field] == null) return@forEach
                tops[field]!!.forEach {
                    counter++
                    top.addContent(
                        UUID.randomUUID(),
                        "" + counter,
                        it.key,
                        it.value
                    )
                }
                top.updateContent()
            }
        }.exceptionOrNull()?.printStackTrace()
    }
}
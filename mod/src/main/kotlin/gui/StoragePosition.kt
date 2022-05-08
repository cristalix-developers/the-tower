package gui

import dev.xdark.clientapi.resource.ResourceLocation
import java.util.*
import kotlin.math.floor

var storage = Storage(
    UUID.randomUUID(),
    "Привет, это тест",
    "Куку у тебя 0",
    "Лол",
    3, 4,
    MutableList(1000) {
        StoragePosition(
            ResourceLocation.of("minecraft", "textures/items/apple.png"),
            (Math.random() * 1000).toInt(),
            "Предмет",
            "описание"
        )
    }
)

class Storage(
    var uuid: UUID,
    var title: String,
    var money: String,
    var hint: String,
    var rows: Int,
    var columns: Int,
    var storage: MutableList<StoragePosition>,
    var page: Int = 1
) {
    private fun getPageSize() = rows * columns

    fun getPagesCount() = floor(storage.size * 1.0 / getPageSize())

    fun getElementsOnPage(pageIndex: Int) = storage.drop(getPageSize() * pageIndex).take(getPageSize())
}

class StoragePosition(
    var texture: ResourceLocation,
    var price: Int,
    var title: String,
    var description: String
)
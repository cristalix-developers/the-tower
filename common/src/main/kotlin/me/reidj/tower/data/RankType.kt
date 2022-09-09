package me.reidj.tower.data

enum class RankType(val title: String) {
    NONE("Без ранга"),
    BRONZE("Бронза"),
    SILVER("Серебро"),
    GOLD("Золото"),
    PLATINUM("Платина"),
    DIAMOND("Алмаз"),
    ;

    fun increaseRank() = if (ordinal >= RankType.values().size - 1) null else RankType.values()[ordinal + 1]

    fun downgradeRank() = if (ordinal >= RankType.values().size - 1) null else RankType.values()[ordinal - 1]
}
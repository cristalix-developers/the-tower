package me.reidj.tower.content

import me.func.mod.data.DailyReward
import me.func.mod.util.nbt
import me.reidj.tower.item
import me.reidj.tower.user.User
import java.util.function.Consumer

enum class DailyRewardType(val reward: DailyReward, val give: Consumer<User>) {
    FIRST(DailyReward("первый день", item {}.nbt("other", "love")), { it.player!!.sendMessage("ура да") }),
    SECOND(DailyReward("второй день", item {}.nbt("other", "love")), { it.player!!.sendMessage("ура да") }),
    THIRD(DailyReward("третий день", item {}.nbt("other", "love")), { it.player!!.sendMessage("ура да") }),
    FOURTH(DailyReward("четвёртвый день", item {}.nbt("other", "love")), { it.player!!.sendMessage("ура да") }),
    FIFTH(DailyReward("пятый день", item {}.nbt("other", "love")), { it.player!!.sendMessage("ура да") }),
    SIXTH(DailyReward("шестой день", item {}.nbt("other", "love")), { it.player!!.sendMessage("ура да") }),
    SEVENTH(DailyReward("седьмой день", item {}.nbt("other", "love")), { it.player!!.sendMessage("ура да") }),
    ;
}
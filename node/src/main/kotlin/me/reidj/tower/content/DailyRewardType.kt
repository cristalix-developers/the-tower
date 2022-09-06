package me.reidj.tower.content

import dev.implario.bukkit.item.item
import me.func.mod.data.DailyReward
import me.reidj.tower.user.User

enum class DailyRewardType(val reward: DailyReward, val give: (User) -> Any) {
    FIRST(DailyReward("первый день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }), { it.player.sendMessage("ура да") }),
    SECOND(DailyReward("второй день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }), { it.player.sendMessage("ура да") }),
    THIRD(DailyReward("третий день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }), { it.player.sendMessage("ура да") }),
    FOURTH(DailyReward("четвёртвый день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }), { it.player.sendMessage("ура да") }),
    FIFTH(DailyReward("пятый день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }), { it.player.sendMessage("ура да") }),
    SIXTH(DailyReward("шестой день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }), { it.player.sendMessage("ура да") }),
    SEVENTH(DailyReward("седьмой день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }), { it.player.sendMessage("ура да") }),
    ;
}
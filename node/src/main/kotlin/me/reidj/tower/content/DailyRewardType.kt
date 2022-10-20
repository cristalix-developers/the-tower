package me.reidj.tower.content

import dev.implario.bukkit.item.item
import me.reidj.tower.user.User
import org.bukkit.inventory.ItemStack

enum class DailyRewardType(val title: String, val icon: ItemStack, val give: (User) -> Any) {
    FIRST("первый день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }, { it.player.sendMessage("ура да") }),
    SECOND("второй день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }, { it.player.sendMessage("ура да") }),
    THIRD("третий день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }, { it.player.sendMessage("ура да") }),
    FOURTH("четвёртвый день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }, { it.player.sendMessage("ура да") }),
    FIFTH("пятый день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }, { it.player.sendMessage("ура да") }),
    SIXTH("шестой день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }, { it.player.sendMessage("ура да") }),
    SEVENTH("седьмой день", item {
        type = org.bukkit.Material.CLAY_BALL
        nbt("other", "love")
    }, { it.player.sendMessage("ура да") }),
    ;
}
package me.reidj.tower

import me.reidj.tower.user.User

interface Game {

    fun end(user: User)

    fun isRating(): Boolean
}
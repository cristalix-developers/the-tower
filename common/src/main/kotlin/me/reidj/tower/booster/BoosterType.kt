package me.reidj.tower.booster

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class BoosterType(
     val title: String,
     val multiplier: Double
) {
     EXP("опыта", 2.0),
     GEM("самоцветов", 2.0),
     TOKEN("токенов", 2.0),
     MONEY("монет", 2.0),
}
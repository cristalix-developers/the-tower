package me.reidj.tower.mob

enum class MobType(
    var hp: Double,
    var damage: Double,
    var wave: IntRange,
) {
    ZOMBIE(3.0, 1.0, 1..5),
    SKELETON( 2.0, 2.5, 3..5),
    HORSE(8.0, 3.0, 10..15),
    POLAR_BEAR(12.0, 1.5, 8..15),
    WOLF(4.0, 2.0, 3..10),
    SPIDER(3.0, 2.0, 1..5),
    ;
}
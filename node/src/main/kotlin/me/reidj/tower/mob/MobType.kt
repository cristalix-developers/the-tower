package me.reidj.tower.mob

enum class MobType(
    var hp: Double,
    var damage: Double,
    var wave: IntRange,
    var isBoss: Boolean
) {
    ZOMBIE(3.0, 1.0, 1..5, false),
    SKELETON( 2.0, 2.5, 3..5, false),
    HORSE(8.0, 3.0, 10..15, false),
    POLAR_BEAR(12.0, 1.5, 8..15, false),
    WOLF(4.0, 2.0, 3..10, false),
    SPIDER(3.0, 2.0, 1..5, false),
    WITHER_SKELETON(30.0, 6.0, 10..10, true),
    ZOMBIE_HORSE(60.0, 12.0, 20..20, true),
    ILLUSIONER(90.0, 18.0, 30..30, true),
    SLIME(120.0, 24.0, 40..40, true),
    ENDERMAN(180.0, 30.0, 50..50, true),
    ;
}
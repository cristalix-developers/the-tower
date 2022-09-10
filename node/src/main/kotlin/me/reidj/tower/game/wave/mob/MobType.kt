package me.reidj.tower.game.wave.mob

enum class MobType(
    var hp: Double,
    var damage: Double,
    var moveSpeed: Float,
    var wave: IntRange,
    var isBoss: Boolean,
) {
    ZOMBIE(3.0, 1.0, 0.01f,1..3, false),
    SKELETON( 2.0, 2.5, 0.02f,3..5, false),
    HORSE(8.0, 3.0, 0.90f,6..15, false),
    POLAR_BEAR(12.0, 1.5, 0.04f,16..25, false),
    WOLF(4.0, 2.0, 0.03f,26..30, false),
    SPIDER(3.0, 2.0, 0.05f,31..35, false),
    WITHER_SKELETON(30.0, 6.0, 0.020f,10..10, true),
    ZOMBIE_HORSE(60.0, 12.0, 0.020f,20..20, true),
    ILLUSIONER(90.0, 18.0, 0.020f,30..30, true),
    SLIME(120.0, 24.0, 0.020f,40..40, true),
    ENDERMAN(180.0, 30.0, 0.020f, 50..50, true),
    ;
}
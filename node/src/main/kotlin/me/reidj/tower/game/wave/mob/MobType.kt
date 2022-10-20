package me.reidj.tower.game.wave.mob

enum class MobType(
    var hp: Double,
    var damage: Double,
    var attackRange: Double,
    var speedAttack: Double,
    var moveSpeed: Float,
    var wave: IntRange,
    var isBoss: Boolean,
    var isShooter: Boolean
) {
    ZOMBIE(2.0, 1.0, 8.0,9.0, 0.01f, 1..3, false, false),
    SKELETON( 3.0, 1.5, 36.0,9.0, 0.02f,3..5, false, true),
    HORSE(8.0, 3.0, 8.0,5.0,0.90f,6..15, false, false),
    POLAR_BEAR(12.0, 1.5, 8.0,7.0,0.04f,16..25, false, false),
    WOLF(4.0, 2.0, 8.0,2.0,0.03f,26..30, false, false),
    SPIDER(9.0, 2.0, 8.0,2.0,0.05f,31..35, false, false),
    WITHER_SKELETON(30.0, 6.0, 8.0,0.1,0.020f,10..10, true, false),
    ZOMBIE_HORSE(60.0, 12.0, 8.0,3.0,0.020f,20..20, true, false),
    ILLUSIONER(90.0, 18.0, 8.0,5.0,0.020f,30..30, true, false),
    SLIME(120.0, 24.0, 8.0,4.0,0.020f,40..40, true, false),
    ENDERMAN(180.0, 30.0, 8.0,3.0,0.020f, 50..50, true, false),
    ;
}
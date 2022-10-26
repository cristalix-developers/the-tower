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
    ZOMBIE(2.0, 1.0, 8.0,6.0, 0.01f, 1..3, false, false),
    SKELETON( 3.0, 1.5, 36.0,9.0, 0.02f,3..5, false, true),
    HORSE(6.0, 2.0, 8.0,8.0,0.90f,6..8, false, false),
    POLAR_BEAR(9.0, 1.0, 8.0,9.0,0.06f,7..9, false, false),
    WOLF(5.0, 1.5, 8.0,6.0,0.01f,8..15, false, false),
    SPIDER(8.0, 2.0, 30.0,8.0,0.05f,10..15, false, true),
    WITHER_SKELETON(20.0, 6.0, 8.0,5.0,0.020f,5..5, true, false),
    ZOMBIE_HORSE(30.0, 12.0, 8.0,7.0,0.020f,10..10, true, false),
    ILLUSIONER(40.0, 18.0, 8.0,5.0,0.020f,15..15, true, false),
    ;
}
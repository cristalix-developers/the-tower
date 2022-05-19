package me.reidj.tower.mob

enum class BossType(
    var hp: Double,
    var damage: Double,
    var wave: IntRange
) {
    WITHER_SKELETON(30.0, 6.0, 10..10),
    ZOMBIE_HORSE(60.0, 12.0, 20..20),
    ILLUSIONER(90.0, 18.0, 30..30),
    SLIME(120.0, 24.0, 40..40),
    ENDERMAN(180.0, 30.0, 50..50),
    ;
}
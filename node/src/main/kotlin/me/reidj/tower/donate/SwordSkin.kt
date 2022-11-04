package me.reidj.tower.donate

import me.reidj.tower.user.User
import me.reidj.tower.util.PATH

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
enum class SwordSkin(
    private val title: String,
    private val price: Long,
) : Donate {
    MITHRIL_SWORD("§3Мифриловый меч", 0),
    SKULL_SPLITTER("§aПожирающий холод", 29),
    ZARICH("§aДемонический меч Зарич", 29),
    DESTROYER_WORLDS("§aРазрушитель миров", 29),
    STEEL_SWORD("§aМеч из осквернённой стали", 29),
    VEIN_RIPPER("§aВспарыватель вен", 29),
    CHASTENER("§aКаратель", 29),
    FLAMING_SWORD("§5Пылающий меч", 49),
    SORCERY("§5Алчущий холод", 49),
    QUENCHER("§5Гаситель надежды", 49),
    CURSE_VOID("§5Проклятье пустоты", 49),
    BONFIRE("§5Костеруб", 49),
    CORRUPTED("§5Осквернённый испепелитель", 49),
    DAMAGE_BLADE("§6Клинок тяжких повреждений", 59),
    SUNWORM("§6Солнцеед", 59),
    WARCRYER("§6Вестник войны", 59),
    HELLSCREAM("§6Колун адского крика", 59),
    BRAIN_CUTTER("§6Мозгорез", 59),
    SOUL_EATER("§6Пожиратель душ", 59),
    ;

    override fun getTitle() = title

    override fun getDescription() = ""

    override fun getTexture() = "$PATH${name.lowercase()}.png"

    override fun getObjectName() = name

    override fun getPrice() = price

    override fun give(user: User) {
        user.stat.donates.add(name)
    }
}
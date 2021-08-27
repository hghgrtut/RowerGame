package by.profs.rowgame.data.competition

enum class CompetitionLevel(val minRowerSkill: Int, val maxRowerSkill: Int) {
    Region(REG_MIN, REG_MAX),
    Republic(REP_MIN, REP_MAX),
    Continental(CON_MIN, CON_MAX),
    World(WOR_MIN, WOR_MAX);

    companion object {
        fun Int.isRegional(): Boolean = this == Region.ordinal
    }
}

private const val REG_MIN = 2
private const val REG_MAX = 8
private const val REP_MIN = 9
private const val REP_MAX = 36
private const val CON_MIN = 16
private const val CON_MAX = 64
private const val WOR_MIN = 36
private const val WOR_MAX = 110
package by.profs.rowgame.data.competition

enum class CompetitionLevel(val rowerSkill: IntRange) {
    Region(2..8), Republic(9..36), Continental(16..64), World(36..110) }
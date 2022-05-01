package by.profs.rowgame.data.competition

import androidx.annotation.StringRes
import by.profs.rowgame.R

enum class CompetitionStrategy(@StringRes val strategyName: Int) {
    START(R.string.strategy_start),
    OVERALL(R.string.strategy_overall),
    FINISH(R.string.strategy_finish)
}
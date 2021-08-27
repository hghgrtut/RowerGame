package by.profs.rowgame.data.competition

import androidx.annotation.DrawableRes
import by.profs.rowgame.R

enum class CompetitionType(@DrawableRes val image: Int) {
    WATER(R.drawable.competition_water),
    CONCEPT(R.drawable.competition_concept),
    OFP(R.drawable.competition_ofp);

    companion object {
        fun Int.isWaterCompetition(): Boolean = this == WATER.ordinal
        fun Int.isOFPCompetition(): Boolean = this == OFP.ordinal
    }
}
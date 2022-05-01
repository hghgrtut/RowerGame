package by.profs.rowgame.data.items.util

import androidx.annotation.DrawableRes
import by.profs.rowgame.R

enum class BoatTypes(val type: String, @DrawableRes val boatImage: Int) {
    SingleScull("1x", R.drawable.boat_single_scull),
    DoubleScull("2x", R.drawable.boat_double_scull), Pair("2-", R.drawable.boat_pair),
    CoxedPair("2+", R.drawable.boat_coxed_pair),
    QuadrupleScull("4x", R.drawable.boat_quadruple_scull),
    Four("4-", R.drawable.boat_four), CoxedFour("4+", R.drawable.boat_coxed_four),
    Eight("8+", R.drawable.boat_eight)
}
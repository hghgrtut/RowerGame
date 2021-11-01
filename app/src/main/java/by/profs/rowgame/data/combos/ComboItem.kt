package by.profs.rowgame.data.combos

import androidx.annotation.DrawableRes

data class ComboItem(
    val rowerId: Int?,
    @DrawableRes val logoBoat: Int,
    @DrawableRes val logoOar: Int,
    val rowerPicUrl: String?,
    val rowerName: String,
    val height: String,
    val rowerWeight: String,
    val rowerAge: String,
    val rigger: String,
    val boatWeight: String,
    val blade: String,
    val oarModel: String,
    val oarWeight: String
)
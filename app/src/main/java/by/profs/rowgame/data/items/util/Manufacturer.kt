package by.profs.rowgame.data.items.util

import androidx.annotation.DrawableRes
import by.profs.rowgame.R

enum class Manufacturer(@DrawableRes val logoResId: Int) {
    Braca(R.drawable.logo_braca), Concept(R.drawable.logo_concept), Croker(R.drawable.logo_croker),
    Empacher(R.drawable.logo_empacher), Filippi(R.drawable.logo_filippi),
    Hudson(R.drawable.logo_hudson), Nemiga(R.drawable.logo_nemiga),
    Peisheng(R.drawable.logo_peisheng), Swift(R.drawable.logo_swift)
}
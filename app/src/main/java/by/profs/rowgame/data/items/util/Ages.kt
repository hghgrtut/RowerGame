package by.profs.rowgame.data.items.util
// age represents maximal age included in this category
enum class Ages(val age: Int, val skillCoef: Double) {
    TooYoung(TY, TY_C),
    Kid(KID, KID_C),
    Jun(JUN, JUN_C),
    Youth(YOUTH, YOUTH_C),
    Adult(ADULT, ADULT_C)
}

private const val TY = 9
private const val KID = 16
private const val JUN = 18
private const val YOUTH = 23
private const val ADULT = 36
private const val TY_C = 0.0
private const val KID_C = 0.5
private const val JUN_C = 0.7
private const val YOUTH_C = 1.0
private const val ADULT_C = 1.2
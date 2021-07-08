package by.profs.rowgame.data.items.util

private const val TY = 9
private const val KID = 16
private const val JUN = 18
private const val YOUTH = 23
private const val ADULT = 36

// age represents maximal age included in this category
enum class Ages(val age: Int, val skillCoef: Double) { TooYoung(TY, 0.0), Kid(KID, 0.5), Jun(JUN, 0.7), Youth(YOUTH, 1.0), Adult(ADULT, 1.2) }
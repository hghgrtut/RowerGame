package by.profs.rowgame.data.items.util

private const val TY = 9
private const val KID = 16
private const val JUN = 18
private const val YOUTH = 23

// age represents maximal age included in this category
enum class Ages(val age: Int) { TooYoung(TY), Kid(KID), Jun(JUN), Youth(YOUTH) }
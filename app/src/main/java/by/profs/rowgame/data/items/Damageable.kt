package by.profs.rowgame.data.items

interface Damageable {
    val id: Int?

    fun broke(damag: Int): Boolean
}
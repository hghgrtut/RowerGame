package by.profs.rowgame.data.items

interface Item {
    val id: Int?

    fun getLevel(): Int

    fun getPower(): Int

    fun broke(damag: Int): Boolean
}
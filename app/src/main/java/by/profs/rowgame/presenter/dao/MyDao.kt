package by.profs.rowgame.presenter.dao

interface MyDao<T, I> {
    fun getItems(): List<T>

    fun insert(item: T)

    fun deleteItem(id: I)

    fun search(id: I): T

    fun updateItem(item: T)
}
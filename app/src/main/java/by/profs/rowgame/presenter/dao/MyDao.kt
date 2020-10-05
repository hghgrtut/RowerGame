package by.profs.rowgame.presenter.dao

interface MyDao<T> {
    fun getItems(): List<T>

    fun insert(item: T)

    fun deleteItem(id: Int)

    fun updateItem(item: T)
}
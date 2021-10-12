package by.profs.rowgame.presenter.database.dao

import kotlinx.coroutines.flow.Flow

interface MyDao<T> {
    fun getItems(): Flow<List<T>>

    fun insert(item: T)

    fun deleteItem(id: Int)

    fun search(id: Int): T?

    fun updateItem(item: T)
}
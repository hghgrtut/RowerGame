package by.profs.rowgame.presenter.informators

interface Informator<T> {
    fun getItemInfo(item: T): List<String>
}
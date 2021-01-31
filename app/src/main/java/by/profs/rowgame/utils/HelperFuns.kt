package by.profs.rowgame.utils

import by.profs.rowgame.presenter.dao.RowerDao

object HelperFuns {
    fun resetInjuries(rowerDao: RowerDao) {
        rowerDao.getItems().forEach { rower ->
            rower.injury = 0
            rowerDao.updateItem(rower)
        }
    }
}
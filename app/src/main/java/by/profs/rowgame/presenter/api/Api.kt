package by.profs.rowgame.presenter.api

import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.RowerExtraInfo

interface Api {
    suspend fun getListOfEventRowers(): List<Rower>?

    suspend fun getRowerExtraInfo(wikiEndpoint: String): RowerExtraInfo?
}
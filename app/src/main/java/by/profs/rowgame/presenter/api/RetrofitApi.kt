package by.profs.rowgame.presenter.api

import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.RowerExtraInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitApi {
    @GET(ENDPOINT_EVENT_ROWER)
    suspend fun getListOfEventRowers(): Response<List<Rower>>

    @GET("{wikiEndpoint}")
    suspend fun getRowerExtraInfo(@Path("wikiEndpoint") wikiEndpoint: String):
            Response<RowerExtraInfo>
}
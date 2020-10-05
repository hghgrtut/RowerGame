package by.profs.rowgame.presenter.api

import android.util.Log
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.RowerExtraInfo
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitApiImplementation : Api {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(API_URL)
        .build()
    private val apiService = retrofit.create(RetrofitApi::class.java)

    override suspend fun getListOfEventRowers(): List<Rower>? {
        val response = apiService.getListOfEventRowers()
        Log.d("Response rower", response.toString())
        return if (response.isSuccessful) { response.body()
        } else { throw IllegalStateException(response.errorBody().toString()) }
    }

    override suspend fun getRowerExtraInfo(wikiEndpoint: String): RowerExtraInfo? {
        val response = apiService.getRowerExtraInfo(wikiEndpoint)
        return if (response.isSuccessful) { response.body()
        } else { throw IllegalStateException(response.errorBody().toString()) }
    }
}
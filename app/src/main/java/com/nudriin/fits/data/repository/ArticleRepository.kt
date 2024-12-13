package com.nudriin.fits.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.nudriin.fits.data.dto.error.ErrorResponse
import com.nudriin.fits.data.pref.UserPreference
import com.nudriin.fits.data.retrofit.ApiService
import com.nudriin.fits.utils.Event
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.toJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class ArticleRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    fun getAllArticle() = liveData {
        emit(Result.Loading)
        try {
            val response =
                apiService.getAllArticle(userPreference.getSession().first().token.toJWT())
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(response, ErrorResponse::class.java)
            emit(Result.Error(Event(body.message)))
        } catch (e: Exception) {
            emit(Result.Error(Event(e.message ?: "An error occurred")))
        }
    }

    companion object {
        @Volatile
        private var instance: ArticleRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): ArticleRepository =
            instance ?: synchronized(this) {
                instance ?: ArticleRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
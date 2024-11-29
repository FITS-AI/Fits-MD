package com.nudriin.fits.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.nudriin.fits.data.dto.error.ErrorResponse
import com.nudriin.fits.data.dto.user.UserLoginRequest
import com.nudriin.fits.data.pref.UserModel
import com.nudriin.fits.data.pref.UserPreference
import com.nudriin.fits.data.retrofit.ApiService
import com.nudriin.fits.utils.Event
import com.nudriin.fits.utils.Result
import retrofit2.HttpException

class AuthRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun login(request: UserLoginRequest) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(request)
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
        private var instance: AuthRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
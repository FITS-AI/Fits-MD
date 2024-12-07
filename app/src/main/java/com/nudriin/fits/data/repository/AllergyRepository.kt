package com.nudriin.fits.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.nudriin.fits.data.dto.allergy.AllergyId
import com.nudriin.fits.data.dto.allergy.AllergyUserSaveRequest
import com.nudriin.fits.data.dto.error.ErrorResponse
import com.nudriin.fits.data.pref.UserPreference
import com.nudriin.fits.data.retrofit.ApiService
import com.nudriin.fits.utils.Event
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.toJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class AllergyRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    fun getAllAllergy() = liveData {
        emit(Result.Loading)
        try {
            val response =
                apiService.getAllAllergy(userPreference.getSession().first().token.toJWT())
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(response, ErrorResponse::class.java)
            emit(Result.Error(Event(body.message)))
        } catch (e: Exception) {
            emit(Result.Error(Event(e.message ?: "An error occurred")))
        }
    }

    fun saveUserAllergy(allergyIds: List<Int>) = liveData {
        emit(Result.Loading)
        try {
            val request = AllergyUserSaveRequest(
                data = allergyIds.map { AllergyId(it) }
            )
            val response =
                apiService.saveAllergy(
                    userPreference.getSession().first().token.toJWT(),
                    request
                )
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val response = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(response, ErrorResponse::class.java)
            emit(Result.Error(Event(body.message)))
        } catch (e: Exception) {
            emit(Result.Error(Event(e.message ?: "An error occurred")))
        }
    }

    fun getAllergyByUserId() = liveData {
        emit(Result.Loading)
        try {
            val response =
                apiService.getUserById(
                    userPreference.getSession().first().token.toJWT(),
                )
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
        private var instance: AllergyRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): AllergyRepository =
            instance ?: synchronized(this) {
                instance ?: AllergyRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
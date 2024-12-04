package com.nudriin.fits.data.retrofit

import com.nudriin.fits.data.dto.allergy.AllergyGetAllResponse
import com.nudriin.fits.data.dto.allergy.AllergyUserSaveRequest
import com.nudriin.fits.data.dto.allergy.AllergyUserSaveResponse
import com.nudriin.fits.data.dto.article.ArticleGetAllResponse
import com.nudriin.fits.data.dto.user.UserGetByIdResponse
import com.nudriin.fits.data.dto.user.UserLoginRequest
import com.nudriin.fits.data.dto.user.UserLoginResponse
import com.nudriin.fits.data.dto.user.UserSaveRequest
import com.nudriin.fits.data.dto.user.UserSaveResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("users")
    suspend fun saveUser(@Body userSaveRequest: UserSaveRequest): UserSaveResponse

    @POST("users/login")
    suspend fun login(@Body loginRequest: UserLoginRequest): UserLoginResponse

    @GET("users/{id}")
    fun getUserById(@Path("id") id: String): Call<UserGetByIdResponse>

    @GET("articles")
    suspend fun getAllArticle(
        @Header("Authorization") token: String
    ): ArticleGetAllResponse

    @GET("allergy")
    suspend fun getAllAllergy(@Header("Authorization") token: String): AllergyGetAllResponse

    @POST("users/allergy")
    suspend fun saveAllergy(
        @Header("Authorization") token: String,
        @Body request: AllergyUserSaveRequest
    ): AllergyUserSaveResponse
}
package com.nudriin.fits.data.retrofit

import com.nudriin.fits.data.dto.user.UserGetByIdResponse
import com.nudriin.fits.data.dto.user.UserLoginRequest
import com.nudriin.fits.data.dto.user.UserLoginResponse
import com.nudriin.fits.data.dto.user.UserSaveRequest
import com.nudriin.fits.data.dto.user.UserSaveResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("users")
    fun saveUser(@Body userSaveRequest: UserSaveRequest): Call<UserSaveResponse>

    @POST("users/login")
    suspend fun login(@Body loginRequest: UserLoginRequest): UserLoginResponse

    @GET("users/{id}")
    fun getUserById(@Path("id") id: String): Call<UserGetByIdResponse>
}
package com.nudriin.fits.data.retrofit

import com.nudriin.fits.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {

        private val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        fun getApiService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.FITS_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }

        fun getGeminiApiService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.GEMINI_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
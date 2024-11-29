package com.nudriin.fits.di

import android.content.Context
import com.nudriin.fits.data.pref.UserPreference
import com.nudriin.fits.data.pref.dataStore
import com.nudriin.fits.data.repository.AuthRepository
import com.nudriin.fits.data.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(userPreference, apiService)
    }
}
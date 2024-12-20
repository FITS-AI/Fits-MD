package com.nudriin.fits.di

import android.content.Context
import com.nudriin.fits.data.pref.UserPreference
import com.nudriin.fits.data.pref.dataStore
import com.nudriin.fits.data.repository.AllergyRepository
import com.nudriin.fits.data.repository.AppSettingsRepository
import com.nudriin.fits.data.repository.ArticleRepository
import com.nudriin.fits.data.repository.AuthRepository
import com.nudriin.fits.data.repository.ProductRepository
import com.nudriin.fits.data.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(userPreference, apiService)
    }

    fun provideArticleRepository(context: Context): ArticleRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return ArticleRepository.getInstance(userPreference, apiService)
    }

    fun provideAllergyRepository(context: Context): AllergyRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AllergyRepository.getInstance(userPreference, apiService)
    }

    fun provideAppSettingsRepository(context: Context): AppSettingsRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return AppSettingsRepository.getInstance(userPreference)
    }

    fun provideProductRepository(context: Context): ProductRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val geminiApiService = ApiConfig.getGeminiApiService()
        val llmApiService = ApiConfig.getLlmApiService()
        return ProductRepository.getInstance(
            userPreference,
            apiService,
            geminiApiService,
            llmApiService
        )
    }
}
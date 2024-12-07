package com.nudriin.fits.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.nudriin.fits.data.pref.SettingsModel
import com.nudriin.fits.data.pref.UserPreference

class AppSettingsRepository private constructor(
    private val userPreference: UserPreference,
) {
    suspend fun saveSettings(settings: SettingsModel) {
        userPreference.saveSettings(settings)
    }

    fun getSettings(): LiveData<SettingsModel> {
        return userPreference.getSettings().asLiveData()
    }

    companion object {
        @Volatile
        private var instance: AppSettingsRepository? = null
        fun getInstance(
            userPreference: UserPreference,
        ): AppSettingsRepository =
            instance ?: synchronized(this) {
                instance ?: AppSettingsRepository(userPreference)
            }.also { instance = it }
    }
}
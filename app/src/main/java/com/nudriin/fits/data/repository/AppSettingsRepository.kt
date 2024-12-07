package com.nudriin.fits.data.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.nudriin.fits.data.pref.SettingsModel
import com.nudriin.fits.data.pref.UserPreference

class AppSettingsRepository private constructor(
    private val userPreference: UserPreference,
) {
    suspend fun saveTheme(darkMode: Boolean) {
        userPreference.saveTheme(darkMode)
    }

    suspend fun saveDiabetes(isDiabetes: Boolean) {
        userPreference.saveDiabetes(isDiabetes)
    }

    suspend fun saveInstruction(instruction: Boolean) {
        userPreference.saveInstruction(instruction)
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
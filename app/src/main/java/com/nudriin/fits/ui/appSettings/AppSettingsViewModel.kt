package com.nudriin.fits.ui.appSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudriin.fits.data.pref.SettingsModel
import com.nudriin.fits.data.repository.AppSettingsRepository
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {
    fun saveSettings(settings: SettingsModel) {
        viewModelScope.launch {
            appSettingsRepository.saveSettings(settings)
        }
    }

    fun getSettings() = appSettingsRepository.getSettings()
}
package com.nudriin.fits.ui.appSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudriin.fits.data.repository.AppSettingsRepository
import kotlinx.coroutines.launch

class AppSettingsViewModel(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {
    fun saveTheme(darkMode: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.saveTheme(darkMode)
        }
    }

    fun saveDiabetes(isDiabetes: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.saveDiabetes(isDiabetes)
        }
    }

    fun saveInstruction(instruction: Boolean) {
        viewModelScope.launch {
            appSettingsRepository.saveInstruction(instruction)
        }
    }

    fun getSettings() = appSettingsRepository.getSettings()
}
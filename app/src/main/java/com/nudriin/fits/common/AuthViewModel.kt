package com.nudriin.fits.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudriin.fits.data.pref.UserModel
import com.nudriin.fits.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            authRepository.saveSession(userModel)
        }
    }

    fun getSession() = authRepository.getSession()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
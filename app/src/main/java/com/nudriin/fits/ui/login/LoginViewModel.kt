package com.nudriin.fits.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudriin.fits.data.dto.user.UserLoginRequest
import com.nudriin.fits.data.pref.UserModel
import com.nudriin.fits.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            authRepository.saveSession(userModel)
        }
    }

    fun login(request: UserLoginRequest) = authRepository.login(request)
}
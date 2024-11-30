package com.nudriin.fits.ui.register

import androidx.lifecycle.ViewModel
import com.nudriin.fits.data.dto.user.UserSaveRequest
import com.nudriin.fits.data.repository.AuthRepository

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun register(request: UserSaveRequest) = authRepository.register(request)
}
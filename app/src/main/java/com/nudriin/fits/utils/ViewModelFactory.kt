package com.nudriin.fits.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nudriin.fits.data.repository.AuthRepository
import com.nudriin.fits.di.Injection
import com.nudriin.fits.ui.login.LoginViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val instance = ViewModelFactory(
                    Injection.provideAuthRepository(context),
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.nudriin.fits.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.common.ProductViewModel
import com.nudriin.fits.data.repository.AllergyRepository
import com.nudriin.fits.data.repository.AppSettingsRepository
import com.nudriin.fits.data.repository.ArticleRepository
import com.nudriin.fits.data.repository.AuthRepository
import com.nudriin.fits.data.repository.ProductRepository
import com.nudriin.fits.di.Injection
import com.nudriin.fits.ui.allergy.AllergyViewModel
import com.nudriin.fits.ui.appSettings.AppSettingsViewModel
import com.nudriin.fits.ui.home.HomeViewModel
import com.nudriin.fits.ui.login.LoginViewModel
import com.nudriin.fits.ui.main.MainViewModel
import com.nudriin.fits.ui.register.RegisterViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val articleRepository: ArticleRepository,
    private val allergyRepository: AllergyRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(articleRepository) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(articleRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }

            modelClass.isAssignableFrom(AllergyViewModel::class.java) -> {
                AllergyViewModel(allergyRepository) as T
            }

            modelClass.isAssignableFrom(AppSettingsViewModel::class.java) -> {
                AppSettingsViewModel(appSettingsRepository) as T
            }

            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(productRepository) as T
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
                    Injection.provideArticleRepository(context),
                    Injection.provideAllergyRepository(context),
                    Injection.provideAppSettingsRepository(context),
                    Injection.provideProductRepository(context),
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
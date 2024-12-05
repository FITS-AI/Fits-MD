package com.nudriin.fits.ui.allergy

import androidx.lifecycle.ViewModel
import com.nudriin.fits.data.repository.AllergyRepository

class AllergyViewModel(
    private val allergyRepository: AllergyRepository
) : ViewModel() {
    fun getAllAllergy() = allergyRepository.getAllAllergy()

    fun saveUserAllergy(request: List<Int>) = allergyRepository.saveUserAllergy(request)

    fun getAllergyByUserId() = allergyRepository.getAllergyByUserId()
}
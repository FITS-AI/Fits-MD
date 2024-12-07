package com.nudriin.fits.ui.scanHistory

import androidx.lifecycle.ViewModel
import com.nudriin.fits.data.repository.ProductRepository

class ScanHistoryViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    fun getAllProduct() = productRepository.getAllProducts()
}
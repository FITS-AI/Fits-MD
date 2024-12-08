package com.nudriin.fits.common

import androidx.lifecycle.ViewModel
import com.nudriin.fits.data.repository.ProductRepository

class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    fun getAllProduct() = productRepository.getAllProducts()
}
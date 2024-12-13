package com.nudriin.fits.common

import androidx.lifecycle.ViewModel
import com.nudriin.fits.data.dto.gemini.GeminiRequest
import com.nudriin.fits.data.dto.llm.LlmRequest
import com.nudriin.fits.data.dto.product.ProductSaveRequest
import com.nudriin.fits.data.repository.ProductRepository

class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    fun getAllProduct() = productRepository.getAllProducts()

    fun saveProduct(request: ProductSaveRequest) = productRepository.saveProduct(request)
    fun generateContent(request: GeminiRequest) = productRepository.generateContent(request)

    fun generateLlm(request: LlmRequest) = productRepository.generateLlm(request)
}
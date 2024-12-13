package com.nudriin.fits.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nudriin.fits.data.repository.ArticleRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    fun getAllArticle() = articleRepository.getAllArticle()
}
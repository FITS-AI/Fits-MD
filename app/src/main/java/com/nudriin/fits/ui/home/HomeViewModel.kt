package com.nudriin.fits.ui.home

import androidx.lifecycle.ViewModel
import com.nudriin.fits.data.repository.ArticleRepository

class HomeViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    fun getAllArticle() = articleRepository.getAllArticle()
}
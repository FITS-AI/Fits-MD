package com.nudriin.fits.ui.articleDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nudriin.fits.R
import com.nudriin.fits.databinding.FragmentArticleDetailBinding

class ArticleDetailFragment : Fragment() {
    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

}
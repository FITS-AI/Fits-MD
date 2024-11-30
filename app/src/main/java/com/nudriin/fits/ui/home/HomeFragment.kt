package com.nudriin.fits.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudriin.fits.adapter.ArticleAdapter
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.data.dto.article.ArticleItem
import com.nudriin.fits.databinding.FragmentHomeBinding
import com.nudriin.fits.ui.camera.CameraActivity
import com.nudriin.fits.ui.welcome.WelcomeActivity
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeArticle.layoutManager = layoutManager

        homeViewModel.getAllArticle().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }

                is Result.Success -> {
                    setArticleList(result.data.article)
                }

                is Result.Error -> {
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(toastText.toString())
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.btnHomeScan.setOnClickListener {
            startCameraX()
        }

        binding.tvSeeAllArticle.setOnClickListener {
            moveToArticleList()
        }
    }

    private fun setArticleList(articleList: List<ArticleItem>) {
        val articles = articleList.take(5)
        val adapter = ArticleAdapter(articles)
        binding.rvHomeArticle.adapter = adapter
        adapter.setOnItemClickCallback(object : ArticleAdapter.OnItemClickCallback {
            override fun onItemClicked(
                articleId: Int,
                title: String,
                author: String,
                content: String,
                imgUrl: String,
                date: String
            ) {
                moveToArticleDetail(articleId, title, author, content, imgUrl, date)
            }

        })
    }

    private fun moveToArticleList() {
        val toArticleList = HomeFragmentDirections.actionHomeFragmentToArticlesListFragment()
        Navigation.findNavController(binding.root).navigate(toArticleList)
    }

    private fun moveToArticleDetail(
        articleId: Int,
        title: String,
        author: String,
        content: String,
        imgUrl: String,
        date: String
    ) {
        val toArticleDetail = HomeFragmentDirections.actionHomeFragmentToArticleDetailFragment(
            articleId, title, author, content, imgUrl, date
        )
        Navigation.findNavController(binding.root).navigate(toArticleDetail)
    }

    private fun startCameraX() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}
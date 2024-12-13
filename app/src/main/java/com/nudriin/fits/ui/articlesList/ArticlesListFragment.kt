package com.nudriin.fits.ui.articlesList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudriin.fits.adapter.ArticleListAdapter
import com.nudriin.fits.data.dto.article.ArticleItem
import com.nudriin.fits.databinding.FragmentArticlesListBinding
import com.nudriin.fits.ui.home.HomeFragmentDirections
import com.nudriin.fits.ui.main.MainViewModel
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.showToast

class ArticlesListFragment : Fragment() {
    private var _binding: FragmentArticlesListBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticlesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupAction()
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(context)
        binding.rvArticleList.layoutManager = layoutManager

        mainViewModel.getAllArticle().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    setArticleList(result.data.article)
                }

                is Result.Error -> {
                    showLoading(false)
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(requireContext(), toastText.toString())
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setArticleList(articleList: List<ArticleItem>) {
        val adapter = ArticleListAdapter(articleList)
        binding.rvArticleList.adapter = adapter
        adapter.setOnItemClickCallback(object : ArticleListAdapter.OnItemClickCallback {
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

    private fun moveToArticleDetail(
        articleId: Int,
        title: String,
        author: String,
        content: String,
        imgUrl: String,
        date: String
    ) {
        val toArticleDetail =
            ArticlesListFragmentDirections.actionArticlesListFragmentToArticleDetailFragment(
                articleId, title, author, content, imgUrl, date
            )
        Navigation.findNavController(binding.root).navigate(toArticleDetail)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}
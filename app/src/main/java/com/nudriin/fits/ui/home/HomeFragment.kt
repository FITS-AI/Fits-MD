package com.nudriin.fits.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudriin.fits.R
import com.nudriin.fits.adapter.ArticleAdapter
import com.nudriin.fits.adapter.ScanHistoryAdapter
import com.nudriin.fits.adapter.ScanHistoryHomeAdapter
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.common.ProductViewModel
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.data.dto.article.ArticleItem
import com.nudriin.fits.data.dto.product.UserHistoryItem
import com.nudriin.fits.databinding.FragmentHomeBinding
import com.nudriin.fits.ui.camera.CameraActivity
import com.nudriin.fits.ui.home.HomeFragmentDirections
import com.nudriin.fits.ui.scanHistory.ScanHistoryFragmentDirections
import com.nudriin.fits.ui.welcome.WelcomeActivity
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.showToast

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val productViewModel: ProductViewModel by viewModels {
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
        authViewModel.getSession().observe(viewLifecycleOwner) { session ->
            binding.tvProfileName.text = resources.getString(R.string.hi_fits_users, session.name)
        }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeArticle.layoutManager = layoutManager

        val scanHistoryLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeHistory.layoutManager = scanHistoryLayoutManager

        homeViewModel.getAllArticle().observe(viewLifecycleOwner) { result ->
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

        productViewModel.getAllProduct().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    if (result.data.userHistory.isNotEmpty()) {
                        binding.rvHomeHistory.visibility = View.VISIBLE
                        binding.ivNotFound.visibility = View.GONE
                        binding.tvNotFound.visibility = View.GONE
                    } else {
                        binding.ivNotFound.visibility = View.VISIBLE
                        binding.tvNotFound.visibility = View.VISIBLE
                    }
                    setScanHistory(result.data.userHistory)
                }

                is Result.Error -> {
                    showLoading(false)
                    binding.ivNotFound.visibility = View.VISIBLE
                    binding.tvNotFound.visibility = View.VISIBLE
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(requireContext(), toastText.toString())
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

        binding.ivHomeProfile.setOnClickListener {
            moveToProfile()
        }

        binding.tvSeeAllHistory.setOnClickListener {
            moveToScanHistory()
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

    private fun setScanHistory(scanHistory: List<UserHistoryItem>) {
        val histories = scanHistory.take(5)
        val adapter = ScanHistoryHomeAdapter(histories)
        binding.rvHomeHistory.adapter = adapter
        adapter.setOnItemClickCallback(object : ScanHistoryHomeAdapter.OnItemClickCallback {
            override fun onItemClicked(
                label: String,
                name: String,
                overall: String,
                sugar: HealthAnalysis,
                fat: HealthAnalysis,
                protein: HealthAnalysis,
                calories: HealthAnalysis,
                assessment: String
            ) {
                moveToScanHistoryDetail(
                    label,
                    name,
                    overall,
                    sugar,
                    fat,
                    protein,
                    calories,
                    assessment
                )
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

    private fun moveToProfile() {
        val toProfile = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
        Navigation.findNavController(binding.root).navigate(toProfile)
    }

    private fun moveToScanHistory() {
        val toScanHistory = HomeFragmentDirections.actionHomeFragmentToScanHistoryFragment()
        Navigation.findNavController(binding.root).navigate(toScanHistory)
    }

    private fun moveToScanHistoryDetail(
        label: String,
        name: String,
        overall: String,
        sugar: HealthAnalysis,
        fat: HealthAnalysis,
        protein: HealthAnalysis,
        calories: HealthAnalysis,
        assessment: String
    ) {
        val toDetail =
            HomeFragmentDirections.actionHomeFragmentToScanHistoryDetailFragment(
                label, name, overall, sugar, fat, protein, calories, assessment
            )

        Navigation.findNavController(binding.root).navigate(toDetail)
    }

    private fun startCameraX() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}
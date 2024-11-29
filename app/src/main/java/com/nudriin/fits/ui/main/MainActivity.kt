package com.nudriin.fits.ui.main

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nudriin.fits.databinding.ActivityMainBinding
import android.Manifest
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nudriin.fits.adapter.ArticleAdapter
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.data.dto.article.ArticleItem
import com.nudriin.fits.ui.camera.CameraActivity
import com.nudriin.fits.ui.welcome.WelcomeActivity
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        setupView()

        binding.btnHomeScan.setOnClickListener {
            startCameraX()
        }
    }

    private fun setupView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeArticle.layoutManager = layoutManager
        authViewModel.getSession().observe(this) { session ->
            if (session.token.isEmpty()) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        mainViewModel.getAllArticle().observe(this@MainActivity) { result ->
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

    private fun setArticleList(articleList: List<ArticleItem>) {
        val articles = articleList.take(5)
        val adapter = ArticleAdapter(articles)
        binding.rvHomeArticle.adapter = adapter
        adapter.setOnItemClickCallback(object : ArticleAdapter.OnItemClickCallback {
            override fun onItemClicked(articleId: String) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
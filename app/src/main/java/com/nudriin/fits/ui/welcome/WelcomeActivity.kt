package com.nudriin.fits.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nudriin.fits.R
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.databinding.ActivityWelcomeBinding
import com.nudriin.fits.ui.login.LoginActivity
import com.nudriin.fits.ui.main.MainActivity
import com.nudriin.fits.utils.ViewModelFactory

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        authViewModel.getSession().observe(this) { session ->
            if (session.token.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, SignupActivity::class.java))
            TODO("CREATE INTENT TO REGISTER")
        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvMessage, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }
}
package com.nudriin.fits.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.nudriin.fits.ui.main.MainActivity
import com.nudriin.fits.R
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.data.dto.user.UserLoginRequest
import com.nudriin.fits.data.pref.UserModel
import com.nudriin.fits.databinding.ActivityLoginBinding
import com.nudriin.fits.ui.welcome.WelcomeActivity
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.getSession().observe(this) { session ->
            if (session.token.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.emailEditText.addTextChangedListener {
            validateEmail()
        }

        binding.passwordEditText.addTextChangedListener {
            validatePassword()
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.login(UserLoginRequest(password, email))
                .observe(this@LoginActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.btnLogin.isEnabled = false
                        }

                        is Result.Success -> {
                            viewModel.saveSession(
                                UserModel(
                                    id = result.data.user.id,
                                    name = result.data.user.name,
                                    email = result.data.user.email,
                                    token = result.data.accessToken,
                                    refreshToken = result.data.refreshToken,
                                    isLogin = true
                                )
                            )
                            Log.d("LoginActivity", result.data.toString())
                            binding.btnLogin.isEnabled = true
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }

                        is Result.Error -> {
                            binding.btnLogin.isEnabled = true
                            result.error.getContentIfNotHandled().let { toastText ->
                                showToast(this, toastText.toString())
                            }
                        }
                    }
                }
        }


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
    }

    private fun validateEmail(): Boolean {
        return when {
            binding.emailEditText.text.toString().isEmpty() -> {
                binding.emailEditTextLayout.error = getString(R.string.email_error_empty)
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text.toString()).matches() -> {
                binding.emailEditTextLayout.error =
                    getString(R.string.email_error_invalid)
                false
            }

            else -> {
                binding.emailEditTextLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        return when {
            binding.passwordEditText.text.toString().isEmpty() -> {
                binding.passwordEditTextLayout.error =
                    getString(R.string.password_error_empty)
                false
            }

            else -> {
                binding.passwordEditTextLayout.error = null
                true
            }
        }
    }
}
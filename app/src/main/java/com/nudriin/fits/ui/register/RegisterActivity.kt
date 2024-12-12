package com.nudriin.fits.ui.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.nudriin.fits.R
import com.nudriin.fits.common.AuthViewModel
import com.nudriin.fits.data.dto.user.UserLoginRequest
import com.nudriin.fits.data.dto.user.UserSaveRequest
import com.nudriin.fits.data.pref.UserModel
import com.nudriin.fits.databinding.ActivityLoginBinding
import com.nudriin.fits.databinding.ActivityRegisterBinding
import com.nudriin.fits.ui.login.LoginActivity
import com.nudriin.fits.ui.main.MainActivity
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.showToast

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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

    fun setupAction() {
        binding.nameEditText.addTextChangedListener {
            validateName()
        }

        binding.emailEditText.addTextChangedListener {
            validateEmail()
        }

        binding.passwordEditText.addTextChangedListener {
            validatePassword()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.register(
                UserSaveRequest(
                    name = name,
                    email = email,
                    password = password
                )
            )
                .observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.btnRegister.isEnabled = false
                        }

                        is Result.Success -> {
                            Log.d("RegisterActivity", result.data.toString())
                            binding.btnRegister.isEnabled = true
                            startActivity(Intent(this, LoginActivity::class.java))
                        }

                        is Result.Error -> {
                            binding.btnRegister.isEnabled = true
                            result.error.getContentIfNotHandled().let { toastText ->
                                showToast(this, toastText.toString())
                            }
                        }
                    }
                }
        }
    }

    private fun validateName(): Boolean {
        return when {
            binding.nameEditText.text.toString().isEmpty() -> {
                binding.nameEditTextLayout.error =
                    getString(R.string.name_error_empty)
                false
            }

            else -> {
                binding.nameEditTextLayout.error = null
                true
            }
        }
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
}
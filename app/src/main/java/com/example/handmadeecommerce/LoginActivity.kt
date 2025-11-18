package com.example.handmadeecommerce

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handmadeecommerce.controller.LoginState
import com.example.handmadeecommerce.controller.UserViewModel
import com.example.handmadeecommerce.controller.UserViewModelFactory
import com.example.handmadeecommerce.databinding.ActivityLoginBinding
import com.example.handmadeecommerce.repository.UserRepositoryImpl
import com.example.handmadeecommerce.utils.switchActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: UserViewModel by viewModels {
        val application = (application as HandmadeEcommerceApp)
        UserViewModelFactory(
            UserRepositoryImpl(application.auth, application.firestore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()

        observeLoginState()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        binding.txtCreateAccount.setOnClickListener {
            switchActivity(this, RegisterActivity::class.java)
        }
    }

    private fun handleLogin() {
        val email = binding.txtEditEmailLogin.text.toString().trim()
        val password = binding.txtEditPasswordLogin.text.toString().trim()

        if (email.isEmpty()) {
            binding.txtEditEmailLogin.error = "Email é obrigatório"
            return
        }
        if (password.isEmpty()) {
            binding.txtEditPasswordLogin.error = "Senha é obrigatória"
            return
        }

        viewModel.loginUser(email, password)
    }

    private fun observeLoginState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Entrando..."
                }
                is LoginState.Success -> {
                    Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()

                    if (state.user.profile == "Artesão") {
                        switchActivity(this, ArtisanProductsActivity::class.java)
                    } else {
                        switchActivity(this, BrowseActivity::class.java)
                    }
                    finish()
                }
                is LoginState.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                    showErrorDialog(state.message)
                }
                is LoginState.Idle -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Erro no Login")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
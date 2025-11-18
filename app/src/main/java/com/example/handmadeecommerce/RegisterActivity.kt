package com.example.handmadeecommerce

import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.handmadeecommerce.controller.UserViewModel
import com.example.handmadeecommerce.controller.UserViewModelFactory
import com.example.handmadeecommerce.controller.RegistrationState
import com.example.handmadeecommerce.databinding.ActivityRegisterBinding
import com.example.handmadeecommerce.model.User
import com.example.handmadeecommerce.repository.UserRepositoryImpl
import com.example.handmadeecommerce.utils.switchActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: UserViewModel by viewModels {
        val application = (application as HandmadeEcommerceApp)
        UserViewModelFactory(
            UserRepositoryImpl(application.auth, application.firestore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupProfileAutoComplete()
        setupListeners()
        observeRegistrationState()
    }

    private fun observeRegistrationState() {
        viewModel.registrationState.observe(this) { state ->
            when (state) {
                is RegistrationState.Loading -> {
                    binding.btnRegisterAccount.isEnabled = false
                    binding.btnRegisterAccount.text = "Registrando..."
                }
                is RegistrationState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    switchActivity(this, LoginActivity::class.java)
                    finish()
                }
                is RegistrationState.Error -> {
                    binding.btnRegisterAccount.isEnabled = true
                    binding.btnRegisterAccount.text = "Criar conta"

                    showErrorDialog(state.message)
                }
                is RegistrationState.Idle -> {
                    binding.btnRegisterAccount.isEnabled = true
                    binding.btnRegisterAccount.text = "Criar conta"
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Erro no Cadastro")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupProfileAutoComplete() {
        val profiles = listOf("Cliente", "Artesão")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, profiles)
        binding.autoCompleteProfile.setAdapter(adapter)
        binding.autoCompleteProfile.setOnClickListener {
            binding.autoCompleteProfile.showDropDown()
        }
    }

    private fun setupListeners() {
        binding.btnRegisterAccount.setOnClickListener {
            registerUser()
        }

        binding.btnLogin.setOnClickListener {
            switchActivity(this, LoginActivity::class.java)
            finish()
        }
    }

    private fun registerUser() {
        val email = binding.txtEditEmail.text.toString().trim()
        val profile = binding.autoCompleteProfile.text.toString()
        val password = binding.txtEditPasswordLogin.text.toString().trim()

        if (!validateInputs(email, password, profile)) {
            return
        }
        val user = User("", email, profile, password)
        viewModel.registerUser(user)
    }

    private fun validateInputs(email: String, pass: String, profile: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.txtEditEmail.error = "Email é obrigatório"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEditEmail.error = "Insira um email válido"
            isValid = false
        }
        if (pass.length < 6) {
            binding.txtEditPasswordLogin.error = "A senha deve ter no mínimo 6 caracteres"
            isValid = false
        }

        if (profile.isEmpty()) {
            binding.autoCompleteProfile.error = "Selecione um perfil"
            isValid = false
        } else if (profile != "Cliente" && profile != "Artesão") {
            binding.autoCompleteProfile.error = "Perfil inválido selecionado"
            isValid = false
        }

        return isValid
    }
}
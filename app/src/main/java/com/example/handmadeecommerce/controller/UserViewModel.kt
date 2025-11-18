package com.example.handmadeecommerce.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handmadeecommerce.model.User
import com.example.handmadeecommerce.repository.UserRepository
import kotlinx.coroutines.launch

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val message: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<RegistrationState>(RegistrationState.Idle)
    val registrationState: LiveData<RegistrationState> = _registrationState

    fun registerUser(user: User) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            val result = repository.registerUser(user)
            result.onSuccess {
                _registrationState.value = RegistrationState.Success("Usuário registrado com sucesso!")
            }.onFailure { exception ->
                _registrationState.value = RegistrationState.Error(exception.message ?: "Ocorreu um erro desconhecido.")
            }
        }
    }

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading


            val loginResult = repository.loginUser(email, password)

            loginResult.onSuccess { authResult ->

                val uid = authResult.user?.uid
                if (uid == null) {
                    _loginState.value = LoginState.Error("Falha ao obter ID do usuário.")
                    return@launch
                }

                val profileResult = repository.getUserProfile(uid)
                profileResult.onSuccess { user ->

                    _loginState.value = LoginState.Success(user)
                }.onFailure { e ->

                    _loginState.value = LoginState.Error("Erro ao buscar perfil: ${e.message}")
                }

            }.onFailure { e ->

                _loginState.value = LoginState.Error("Email ou senha inválidos.")
            }
        }
    }
}
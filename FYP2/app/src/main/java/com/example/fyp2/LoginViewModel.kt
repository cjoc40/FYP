package com.example.fyp2

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    val currentUser = repository.currentUser
    val hasUser : Boolean
        get() = repository.hasUser()

    var loginUIState by mutableStateOf(LoginUIState())
        private set

    fun onUserNameChange(userName: String){
        loginUIState = loginUIState.copy(userName = userName)
    }
    fun onPasswordNameChange(password: String){
        loginUIState = loginUIState.copy(password = password)
    }
    fun onUserNameChangeSignUP(userName: String){
        loginUIState = loginUIState.copy(userNameSignUp = userName)
    }
    fun onPasswordChangeSignup(password: String){
        loginUIState = loginUIState.copy(passwordSignUp = password)
    }
    fun onConfirmPasswordChange(password: String){
        loginUIState = loginUIState.copy(confirmPasswordSignUp = password)
    }



    private fun validateSignUpForm() =
        loginUIState.userNameSignUp.trim().isNotBlank() &&
                loginUIState.passwordSignUp.trim().isNotBlank() &&
                loginUIState.confirmPasswordSignUp.trim().isNotBlank()


    private fun registerUser(email: String, password: String, navController: NavHostController) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    navController.navigate("CourseSetup")
                } else {
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    Log.e("Registration", errorMessage)
                }
            }
    }

    fun loginUser(email: String, password: String, context: Context, navController: NavHostController) = viewModelScope.launch {
        try {
            loginUIState = loginUIState.copy(isLoading = true)

            loginUIState = loginUIState.copy(loginError = null)
            repository.login(email, password) { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "success login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUIState = loginUIState.copy(isSuccessfulLogin = true)
                    navController.navigate("Gamemode")
                } else {
                    Toast.makeText(
                        context,
                        "Failed login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUIState = loginUIState.copy(isSuccessfulLogin = false)
                }
            }
        } catch (e: Exception) {
            loginUIState = loginUIState.copy(loginError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUIState = loginUIState.copy(isLoading = false)
        }
    }


}

data class LoginUIState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmPasswordSignUp: String = "",
    val isLoading: Boolean  = false,
    val isSuccessfulLogin: Boolean = false,
    val signUpError :String? = null,
    val loginError: String? = null
)

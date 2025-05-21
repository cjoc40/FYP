package com.example.fyp2.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fyp2.LoginViewModel
import com.example.fyp2.R

@Composable
fun Login2(navController: NavHostController, viewModel: LoginViewModel, context: Context) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val loginError = remember { viewModel.loginUIState.loginError }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(
                    red = 0.6392157f,
                    green = 0.5803922f,
                    blue = 0.38039216f,
                    alpha = 1f
                )
            )
            .padding(0.dp)
            .alpha(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White
                )
                .padding(0.dp)
                .alpha(1f)
                .clip(RoundedCornerShape(0.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 1f))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.tus),
                    contentDescription = null,
                    modifier = Modifier
                        .size(210.dp)
                        .align(Alignment.CenterStart)
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 160.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Tic-Tac-Toe\n" +
                        "         X\n" +
                        " Connect 4",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Email:",
                        fontSize = 20.sp,
                        )
                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 10.dp, top = 20.dp)
                            .width(200.dp)
                            .height(28.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp)),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Password:",
                        fontSize = 20.sp,
                        )
                    OutlinedTextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(200.dp)
                            .height(28.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        )
                    )
                }
                Button(
                    onClick = {
                        Log.d("Login", "Email: ${emailState.value}, Password: ${passwordState.value}")
                        viewModel.loginUser(emailState.value, passwordState.value, context,navController = navController)
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(255 , 135, 135))
                ) {
                    Text("Login",
                        color = Color.Black)
                }
                if (loginError != null) {
                    Log.e("Login", "Login failed: $loginError")
                }

                ClickableText(
                    text = AnnotatedString("Don't have an account?"),
                    onClick = {
                        navController.navigate("Login")
                    },
                )
            }
        }
    }
}

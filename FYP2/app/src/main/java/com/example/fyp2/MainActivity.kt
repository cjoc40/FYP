package com.example.fyp2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fyp2.theme.fypTheme
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            fypTheme {
                val navController = rememberNavController()
                SetupNavHost(navController)
            }
        }
    }
}

@Composable
fun Login(navController: NavHostController) {

    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
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
                    .background(Color.Black)
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
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        textDecoration = TextDecoration.None,
                        letterSpacing = 0.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(bottom = 8.dp, top = 30.dp)
                            .alpha(1f),
                        color = Color(red = 0f, green = 0f, blue = 0f, alpha = 1f),
                        fontStyle = FontStyle.Normal,
                    )
                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = { emailState.value = it },
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 0.dp)
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
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        textDecoration = TextDecoration.None,
                        letterSpacing = 0.sp,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .alpha(1f),
                        color = Color(red = 0f, green = 0f, blue = 0f, alpha = 1f),
                        fontStyle = FontStyle.Normal,
                    )
                    OutlinedTextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        modifier = Modifier
                            .padding(start = 8.dp, top = 0.dp)
                            .width(200.dp)
                            .height(28.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp)),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        )
                    )
                }
                Button(
                    onClick = {
                        registerUser(emailState.value, passwordState.value, navController)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(110 , 231, 255))
                ) {
                    Text("Register",
                        color = Color.Black)
                }
                ClickableText(
                    text = AnnotatedString("Have an account?"),
                    onClick = {
                        navController.navigate("login2")
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .alpha(1f)

                )
            }
        }
    }
}
private fun registerUser(email: String, password: String, navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                navController.navigate("Gamemode")
            } else {
                val errorMessage = task.exception?.message ?: "Registration failed"
                Log.e("Registration", errorMessage)
            }
        }
}
@Composable
fun ClickableImage(resourceId: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(resourceId),
        contentDescription = null,
        modifier = modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    fypTheme {
        val navController = rememberNavController()
        Login(navController = navController)
    }
}


package com.example.fyp2.pages

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fyp2.ClickableImage
import com.example.fyp2.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Account(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
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
                    .padding(top = 200.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Account settings",
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = 16.dp),
                ) {
                    Text("Privacy", color = Color.White)
                }
                Button(
                    onClick = {
                        navController.navigate("contact")
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = 16.dp),
                ) {
                    Text("Contact us", color = Color.White)
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = 16.dp),
                ) {
                    Text("Personal info", color = Color.White)
                }
                Button(
                    onClick = {
                        navController.navigate("login2")
                        auth.signOut()
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(top = 16.dp),
                ) {
                    Text("Sign out", color = Color.White)
                }
            }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(42, 53, 64))
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 80.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClickableImage(
                            resourceId = R.drawable.profile,
                            onClick = {
                                navController.navigate("Account")
                            },
                            modifier = Modifier.size(80.dp)
                        )
                        ClickableImage(
                            resourceId = R.drawable.home,
                            onClick = {
                                navController.navigate("Gamemode")
                            },
                            modifier = Modifier.size(80.dp)
                        )
                        ClickableImage(
                            resourceId = R.drawable.stats,
                            onClick = {
                                navController.navigate("StatsStyle")
                            },
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }
            }
        }
    }


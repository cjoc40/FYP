package com.example.fyp2.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fyp2.ClickableImage
import com.example.fyp2.R

@Composable
fun TicPlaystyle(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
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
                contentDescription = "Tic Tac Toe Logo",
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.CenterStart)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Playstyle for Tic Tac Toe",
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navController.navigate("GridScreen/PlayerVsPlayer") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color(110 , 231, 255))
            ) {
                Text("Player vs Player", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("GridScreen/PlayerVsAI") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color(255 , 135, 135))
            ) {
                Text("Player vs Minimax", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("GridScreen/PlayerVsMCTS") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color.Gray)
            ) {
                Text("Player vs MCTS", color = Color.White)
            }
        }
        Button(onClick = { navController.popBackStack()},
            modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(42, 53, 64)),
            contentAlignment = Alignment.BottomCenter
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
                    onClick = { navController.navigate("Account") },
                    modifier = Modifier.size(80.dp),
                )
                ClickableImage(
                    resourceId = R.drawable.home,
                    onClick = { navController.navigate("Gamemode") },
                    modifier = Modifier.size(80.dp),
                )
                ClickableImage(
                    resourceId = R.drawable.stats,
                    onClick = { navController.navigate("StatsStyle") },
                    modifier = Modifier.size(80.dp),
                )
            }
        }
    }
}

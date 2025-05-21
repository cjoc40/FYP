package com.example.fyp2.pages

import com.example.fyp2.TicTacToeViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fyp2.ClickableImage
import com.example.fyp2.R

@Composable
fun Stats(navController: NavController, viewModel: TicTacToeViewModel) {
    val allGameStats = viewModel.allGamesStats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 36.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Text("AI Game Stats", fontSize = 18.sp)
            }

            if (allGameStats.isEmpty()) {
                item {
                    Text("No games played yet.", fontSize = 16.sp)
                }
            } else {
                items(allGameStats.size) { gameIndex ->
                    val gameStats = allGameStats[gameIndex]
                    Column(Modifier.padding(8.dp)) {
                        if (gameStats.modelUsed == "MCTS") {
                            Text(
                                "Game #${gameIndex + 1}, Algorithm: MCTS, ${gameStats.simulations} Sims",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (gameStats.modelUsed == "Minimax") {
                            Text(
                                "Game #${gameIndex + 1}, Algorithm: Minimax, Pruning: ${gameStats.PruningEnabled}, Difficulty: ${gameStats.difficulty}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (gameStats.moves.isEmpty()) {
                            Text("No moves made in this game.", fontSize = 14.sp)
                        } else {
                            gameStats.moves.forEachIndexed { index, move ->
                                Column {
                                    Text("Move ${index + 1} (${move.first}, ${move.second}):", fontSize = 12.sp)
                                    if (gameStats.resourceStats.size > index) {
                                        val resourceStat = gameStats.resourceStats[index]
                                        Text("  CPU Usage: ${resourceStat.cpuUsage} ms", fontSize = 12.sp)
                                        Text("  Memory Used: ${resourceStat.memoryUsed} KB", fontSize = 12.sp)
                                        Text("  Time to Move: ${resourceStat.timeToMove} ms", fontSize = 12.sp)
                                    } else {
                                        Text("  No resource stats available.", fontSize = 12.sp)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        Button(onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(42, 53, 64)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClickableImage(
                    resourceId = R.drawable.profile,
                    onClick = { navController.navigate("Account") },
                    modifier = Modifier.size(80.dp)
                )
                ClickableImage(
                    resourceId = R.drawable.home,
                    onClick = { navController.navigate("Gamemode") },
                    modifier = Modifier.size(80.dp)
                )
                ClickableImage(
                    resourceId = R.drawable.stats,
                    onClick = { navController.navigate("StatsStyle") },
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

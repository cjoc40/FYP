package com.example.fyp2.pages

import Connect4ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fyp2.ClickableImage
import com.example.fyp2.R
import com.example.fyp2.TicTacToeViewModel
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.BarChartData.Bar

@Composable
fun StatCompare(navController: NavHostController, viewModel: TicTacToeViewModel, viewModel2: Connect4ViewModel) {
    val ticTacToeStats = viewModel.allGamesStats
    val connect4Stats = viewModel2.allGamesStats

    val ticTacToeAvgMemoryUsage = ticTacToeStats.flatMap { it.resourceStats.map { it.memoryUsed.toFloat() } }.average().toFloat()
    val connect4AvgMemoryUsage = connect4Stats.flatMap { it.resourceStats.map { it.memoryUsed.toFloat() } }.average().toFloat()

    val ticTacToeAvgCpuUsage = ticTacToeStats.flatMap { it.resourceStats.map { it.cpuUsage.toFloat() } }.average().toFloat()
    val connect4AvgCpuUsage = connect4Stats.flatMap { it.resourceStats.map { it.cpuUsage.toFloat() } }.average().toFloat()

    val ticTacToeTimePerMove = ticTacToeStats.map { game ->
        game.resourceStats.map { it.timeToMove.toFloat() }.average()
    }.filter { it.isFinite() }

    val ticTacToeAvgTime = if (ticTacToeTimePerMove.isEmpty()) {
        0f
    } else {
        ticTacToeTimePerMove.average()
    }

    val connect4TimePerMove = connect4Stats.map { game ->
        game.resourceStats.map { it.timeToMove.toFloat() }.average()
    }.filter { it.isFinite() }

    val ticTacToeWinLossRatio = viewModel.calculateWinLossRatio()
    val connect4WinLossRatio = viewModel2.calculateWinLossRatio()

    val ticTacToeAvgDuration = viewModel.calculateAvgDuration()
    val connect4AvgDuration = viewModel2.calculateAvgDuration()

    var barChartData by remember {
        mutableStateOf(
            BarChartData(
                bars = listOf(
                    Bar(label = "Tic Tac Toe", value = ticTacToeAvgTime.toFloat(), color = Color(255 , 135, 135)),
                    Bar(label = "Connect Four",
                        value = if (connect4TimePerMove.isNotEmpty()) connect4TimePerMove.average()
                            .toFloat() else 0f,
                        color = Color(110 , 231, 255)
                    )
                )
            )
        )
    }
    var memoryChartData by remember {
        mutableStateOf(
            BarChartData(
                bars = listOf(
                    Bar(label = "Tic Tac Toe", value = ticTacToeAvgMemoryUsage, color = Color(255 , 135, 135)),
                    Bar(label = "Connect Four", value = connect4AvgMemoryUsage, color = Color(110 , 231, 255))
                )
            )
        )
    }
    var cpuChartData by remember {
        mutableStateOf(
            BarChartData(
                bars = listOf(
                    Bar(label = "Tic Tac Toe", value = ticTacToeAvgCpuUsage, color = Color(255 , 135, 135)),
                    Bar(label = "Connect Four", value = connect4AvgCpuUsage, color = Color(110 , 231, 255))
                )
            )
        )
    }

    val lastTicTacToeModel = ticTacToeStats.lastOrNull()?.modelUsed ?: "Unknown"
    val lastConnect4Model = connect4Stats.lastOrNull()?.modelUsed ?: "Unknown"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 36.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp)
        ) {
            item {
                Column {
                    Text("Last Models used:")
                    Text("Tic Tac Toe: $lastTicTacToeModel", fontSize = 16.sp, color = Color.Gray)
                    Text("Connect Four: $lastConnect4Model", fontSize = 16.sp, color = Color.Gray)
                }
            }
            item {
                Column {
                    Text("Average Time per Move (ms)", fontSize = 18.sp)
                    BarChart(
                        barChartData = barChartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                            .padding(bottom = 25.dp, top = 25.dp)
                    )
                }
            }
            item {
                Column {
                    Text("Average Memory Usage (MB)", fontSize = 18.sp)
                    BarChart(
                        barChartData = memoryChartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(bottom = 25.dp)
                    )
                }
            }
            item {
                Column {
                    Text("Average CPU Usage (%)", fontSize = 18.sp)
                    BarChart(
                        barChartData = cpuChartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(bottom = 25.dp)
                    )
                }
            }
            item {
                Column {
                    Text("Win/Loss Ratio", fontSize = 18.sp)
                    BarChart(
                        barChartData = BarChartData(
                            bars = listOf(
                                Bar(label = "Tic Tac Toe", value = ticTacToeWinLossRatio, color = Color(255, 135, 135)),
                                Bar(label = "Connect Four", value = connect4WinLossRatio, color = Color(110, 231, 255))
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(bottom = 25.dp)
                    )
                }
            }
            item {
                Column {
                    Text("Average Duration (ms)", fontSize = 18.sp)
                    BarChart(
                        barChartData = BarChartData(
                            bars = listOf(
                                Bar(label = "Tic Tac Toe", value = ticTacToeAvgDuration.toFloat(), color = Color(255, 135, 135)),
                                Bar(label = "Connect Four", value = connect4AvgDuration.toFloat(), color = Color(110, 231, 255))
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(bottom = 25.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                viewModel.resetStats()
                viewModel2.resetStats()
                barChartData = BarChartData(
                    bars = listOf(
                        Bar(label = "Tic Tac Toe", value = 0f, color = Color.Blue),
                        Bar(label = "Connect Four", value = 0f, color = Color.Red)
                    )
                )
            }) {
                Text("Reset stats")
            }
            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
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
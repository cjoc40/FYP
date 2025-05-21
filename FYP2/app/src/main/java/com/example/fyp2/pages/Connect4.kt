package com.example.fyp2.pages

import Connect4ViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fyp2.ClickableImage
import com.example.fyp2.MCTS
import com.example.fyp2.pages.Player
import com.example.fyp2.R
import com.example.fyp2.TicTacToeViewModel

@Composable
fun Connect4(viewModel: Connect4ViewModel, navController: NavHostController, playstyle: String) {
    var currentPlayer by remember { mutableStateOf(viewModel.getCurrentPlayer()) }
    var gameBoard by remember { mutableStateOf(viewModel.getBoard()) }
    var playerXName by remember { mutableStateOf(viewModel.playerXName) }
    var playerOName by remember { mutableStateOf(viewModel.playerOName) }
    var winner by remember { mutableStateOf<Player?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    var draw by remember { mutableStateOf(false) }
    val context = LocalContext.current
    draw = viewModel.isDraw()
    var simulations by remember { mutableStateOf(viewModel.mcts.simulations) }
    val isPruningEnabled = viewModel.isPruningEnabled

    LaunchedEffect(Unit) {
        gameBoard = viewModel.getBoard()
        currentPlayer = viewModel.getCurrentPlayer()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        if (playstyle == "PlayerVsPlayer") {
            TextField(
                value = playerXName,
                onValueChange = { newValue -> playerXName = newValue },
                label = { Text("Enter Player 1 Name") }
            )

            TextField(
                value = playerOName,
                onValueChange = { newValue -> playerOName = newValue },
                label = { Text("Enter Player 2 Name") }
            )
        }
        Text(
            text = "Connect 4",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            fontSize = 24.sp
        )

        for (row in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    GridItem2(
                        content = gameBoard[row][col],
                        onSquareClick = {
                            if (!gameOver) {
                                val lowestAvailableRow =
                                    (5 downTo 0).firstOrNull { gameBoard[it][col] == null }
                                if (lowestAvailableRow != null) {
                                    gameBoard[lowestAvailableRow][col] = currentPlayer
                                    viewModel.onSquareClick(lowestAvailableRow, col)

                                    gameOver = viewModel.gameOver()
                                    draw = viewModel.isDraw()

                                    if (gameOver) {
                                        winner = currentPlayer
                                    } else if (playstyle == "PlayerVsAI") {
                                        currentPlayer = Player.Red
                                        val aiMove = viewModel.mctsMove()
                                        if (aiMove != null) {
                                            gameBoard[aiMove.first][aiMove.second] = currentPlayer
                                            viewModel.onSquareClick(aiMove.first, aiMove.second)
                                            gameOver = viewModel.gameOver()
                                            draw = viewModel.isDraw()

                                            if (gameOver) {
                                                winner = currentPlayer
                                            }
                                        }
                                        currentPlayer = Player.Black
                                    } else {
                                        currentPlayer =
                                            if (currentPlayer == Player.Black) Player.Red else Player.Black
                                    }
                                    if (playstyle == "PlayerVsMinimax" && currentPlayer == Player.Red) {
                                        val aiMove = viewModel.minimaxMove()
                                        if (aiMove != null) {
                                            gameBoard[aiMove.first][aiMove.second] = currentPlayer
                                            viewModel.onSquareClick(aiMove.first, aiMove.second)
                                            gameOver = viewModel.gameOver()
                                            draw = viewModel.isDraw()
                                            if (gameOver) {
                                                winner = currentPlayer
                                            }
                                        }
                                        currentPlayer = Player.Black
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        if (gameOver) {
            val winningPlayerName = if (winner == Player.Black) playerXName else playerOName
            Text(
                text = "Player $winningPlayerName has won!",
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 23.dp),
                fontSize = 20.sp
            )
        } else if (draw) {
            Text(
                text = "Draw!",
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 23.dp),
                fontSize = 20.sp
            )
        } else {
            val currentName = if (currentPlayer == Player.Black) playerXName else playerOName
            Text(
                text = "Player ${currentName}'s turn",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp
            )
        }
        Row() {
            Button(
                onClick = {
                    currentPlayer = Player.Black
                    viewModel.resetGame()
                    gameBoard = viewModel.getBoard()
                    gameOver = false
                    winner = null
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(150.dp)
                    .height(50.dp)
                    .padding(start = 5.dp, end = 10.dp),
                colors = ButtonDefaults.buttonColors(Color(110, 231, 255))
            ) {
                Text(text = "New Game", fontSize = 16.sp, color = Color.Black)
            }
            if (playstyle != "PlayerVsPlayer") {
                Text(
                    "Difficulty",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 90.dp, top = 40.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (playstyle != "PlayerVsPlayer") {
                Button(
                    onClick = {
                        navController.navigate("MctsStats")
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .height(50.dp)
                        .padding(start = 5.dp, end = 10.dp),
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text(text = "Stats", fontSize = 16.sp)
                }
            }
            if (playstyle == "PlayerVsAI") {
                Text("Easy", fontSize = 16.sp)
                Slider(
                    value = simulations.toFloat(),
                    onValueChange = { newValue ->
                        simulations = newValue.toInt()
                        viewModel.mcts.simulations = newValue.toInt()
                    },
                    valueRange = 100f..8000f,
                    steps = 79,
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
                Text("Hard", fontSize = 16.sp)
            }
            if (playstyle == "PlayerVsMinimax") {
                Text("Easy", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                Slider(
                    value = (2 - viewModel.difficulty.ordinal).toFloat(),
                    onValueChange = {
                        viewModel.difficulty =
                            Difficulty.entries[2 - it.toInt()]
                    },
                    valueRange = 0f..2f,
                    steps = 2,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            if (playstyle == "PlayerVsMinimax") {
                Text(
                    "Pruning",
                    fontSize = 16.sp,
                )
                Switch(
                    modifier = Modifier.padding(end = 12.dp),
                            checked = isPruningEnabled,
                    onCheckedChange = {
                        viewModel.togglePruning()
                        viewModel.isPruningEnabled = it
                        viewModel.resetGame()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(110, 231, 255),
                        uncheckedThumbColor = Color(255, 135, 135)
                    )
                )
            }
            Button(
                onClick = { navController.popBackStack() },
            ) {
                Text("Back")
            }
        }
    }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 700.dp)
                .background(Color(42, 53, 64)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 78.dp),
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

@Composable
fun GridItem2(content: Player?, onSquareClick: (Player?) -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(Color(0xFFBFD7ED))
            .clickable {
                onSquareClick(content)
            },
        contentAlignment = Alignment.Center
    ) {
        content?.let {
            val image = painterResource(id = com.example.fyp2.pages.Player.valueOf(it.name).imageResource)
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
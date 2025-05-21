package com.example.fyp2.pages


import com.example.fyp2.TicTacToeViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fyp2.ClickableImage
import com.example.fyp2.R

//LazyColumn {
//    item {
//        if (isPortrait()) {
//            GridScreen(ticTacToeViewModel)
//        } else {
//            LandscapeGridScreen(ticTacToeViewModel)
//        }
//    }
//}

@Composable
fun GridScreen(viewModel: TicTacToeViewModel, navController: NavHostController, playstyle: String) {
    var gameBoard by remember { mutableStateOf(viewModel.getBoard()) }
    var currentPlayer by remember { mutableStateOf(viewModel.getPlayer()) }
    var playerXName = viewModel.playerXName
    var playerOName = viewModel.playerOName
    val context = LocalContext.current
    val isPruningEnabled = viewModel.isPruningEnabled
    var simulations by remember { mutableStateOf(viewModel.mcts.simulations) }

    var winner by remember { mutableStateOf<Player?>(null) }
    var gameOver by remember { mutableStateOf(false) }
    var draw by remember { mutableStateOf(false) }
    draw = viewModel.isDraw()

    LaunchedEffect(Unit) {
        viewModel.restoreGameState()
        gameBoard = viewModel.getBoard()
        currentPlayer = viewModel.getPlayer()
    }

    LaunchedEffect(gameBoard) {
        if (currentPlayer == Player.O && !viewModel.gameOver() && !viewModel.isDraw()) {
            val aiMove = viewModel.playAI(context)
            if (aiMove != null) {
                val (row, col) = aiMove
                gameBoard = gameBoard.copyOf().apply { this[row][col] = Player.O }
                viewModel.onSquareClick(row, col)
                currentPlayer = viewModel.getPlayer()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        if(playstyle == "PlayerVsPlayer") {
            TextField(
                value = playerXName,
                onValueChange = { newValue -> playerXName = newValue },
                label = { Text("Enter Player X Name") }
            )

            TextField(
                value = playerOName,
                onValueChange = { newValue -> playerOName = newValue },
                label = { Text("Enter Player O Name") }
            )
        }
        Text(
            text = "Tic Tac Toe",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            fontSize = 24.sp
        )

        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 3) {
                    GridItem(
                        content = gameBoard[row][col],
                        onSquareClick = { content ->
                            if (gameBoard[row][col] == null && !gameOver) {
                                // Player move
                                gameBoard[row][col] = currentPlayer
                                viewModel.onSquareClick(row, col)
                                gameOver = viewModel.gameOver()

                                if (!gameOver) {
                                    currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
                                    if (playstyle == "PlayerVsAI" && currentPlayer == Player.O) {
                                        viewModel.playAI(context)
                                        gameBoard = viewModel.getBoard()
                                        gameOver = viewModel.gameOver()
                                        if (gameOver) {
                                            winner = Player.O
                                        } else {
                                            currentPlayer = Player.X
                                        }
                                    }
                                    if (playstyle == "PlayerVsMCTS" && currentPlayer == Player.O) {
                                            viewModel.playAI2(context)?.let {
                                                gameBoard = viewModel.getBoard()
                                                gameOver = viewModel.gameOver()
                                                draw = viewModel.isDraw()
                                                if (gameOver) {
                                                    winner = Player.O
                                                } else if (!draw) {
                                                    currentPlayer = Player.X
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    winner = viewModel.getWinner()
                                }
                        }
                    )
                }
            }
        }

        if (gameOver) {
            val winningPlayerName = if (winner == Player.X) playerXName else playerOName
            Text(
                text = "Player $winningPlayerName has won!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 23.dp),
                fontSize = 20.sp
            )

        } else if (draw) {
            Text(
                text = "Draw!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 23.dp),
                fontSize = 20.sp
            )
        } else {
            val currentName = if (currentPlayer == Player.X) playerXName else playerOName
            Text(
                text = "Player ${currentName}'s turn",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                fontSize = 20.sp
            )
        }
        Row {
            Button(
                onClick = {
                    currentPlayer = Player.X
                    gameBoard = Array(3) { Array(3) { null } }
                    gameOver = false
                    viewModel.resetGame()
                },
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color(110, 231, 255))
            ) {
                Text(
                    text = "New Game", fontSize = 16.sp,
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playstyle == "PlayerVsAI") {
                    Text("Difficulty<", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
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
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playstyle == "PlayerVsAI") {
                    Text(
                        "Pruning",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Switch(
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
                if (playstyle == "PlayerVsMCTS") {
                    Text("Easy", fontSize = 16.sp)
                    Slider(
                        value = simulations.toFloat(),
                        onValueChange = { newValue ->
                            simulations = newValue.toInt()
                            viewModel.mcts.simulations = newValue.toInt()
                        },
                        valueRange = 100f..3000f,
                        steps = 79,
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                    )
                    Text("Hard", fontSize = 16.sp)
                }
            }
            if (playstyle != "PlayerVsPlayer") {
                Button(
                    onClick = {
                        navController.navigate("Stats")
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
        }
        Button(onClick = { navController.popBackStack()},
            modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .height(390.dp)
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
}

@Composable
fun LandscapeGridScreen(viewModel: TicTacToeViewModel) {
    var currentPlayer by remember { mutableStateOf(viewModel.getPlayer()) }
    var gameBoard by remember { mutableStateOf(viewModel.getBoard()) }
    var playerXName by remember { mutableStateOf(viewModel.playerXName) }
    var playerOName by remember { mutableStateOf(viewModel.playerOName) }
    var gameOver by remember { mutableStateOf(false) }
    var draw by remember { mutableStateOf(false) }
    draw = viewModel.isDraw()
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Left column
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 3) {
                        GridItem(
                            content = gameBoard[row][col],
                            onSquareClick = { content ->
                                if (gameBoard[row][col] == null && !gameOver) {
                                    gameBoard[row][col] = currentPlayer
                                    currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
                                    viewModel.onSquareClick(row, col)
                                    gameOver = viewModel.gameOver()

                                    if (!gameOver && currentPlayer == Player.O) {
                                        viewModel.playAI(context)
                                        gameOver = viewModel.gameOver()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Right column
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = playerXName,
                onValueChange = { newValue -> playerXName = newValue },
                label = { Text("Enter Player X Name") }
            )

            TextField(
                value = playerOName,
                onValueChange = { newValue -> playerOName = newValue },
                label = { Text("Enter Player O Name") })
            Button(
                onClick = {
                    currentPlayer = Player.X
                    gameBoard = Array(3) { Array(3) { null } }
                    gameOver = false
                    viewModel.resetGame()
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            ) {
                Text(text = "New Game", fontSize = 20.sp)
            }


            if (gameOver) {
                val winningPlayerName = if (currentPlayer == Player.X) playerXName else playerOName
                Text(
                    text = "Player $winningPlayerName has won!",
                    fontSize = 20.sp
                )

            } else if (draw) {
                Text(
                    text = "Draw!",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 23.dp),
                    fontSize = 20.sp
                )
            }
            else {
                Text(
                    text = "Player ${currentPlayer.name}'s turn",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun GridItem(content: Player?, onSquareClick: (Player?) -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color(0xFFBFD7ED))
            .clickable {
                onSquareClick(content)
            },
        contentAlignment = Alignment.Center
    ) {
        content?.let {
            val image = painterResource(id = it.imageResource)
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

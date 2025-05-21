package com.example.fyp2

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.fyp2.pages.Difficulty
import com.example.fyp2.pages.Player
import com.example.fyp2.pages.ResourceStats

data class GameStats(
    val gameId: Int,
    val moves: List<Pair<Int, Int>>,
    val resourceStats: List<ResourceStats>,
    var simulations: Int,
    var PruningEnabled: Boolean = false,
    var difficulty: Difficulty,
    val modelUsed: String,
    var winRatio: Float = 0f,
    var avgDuration: Long = 0
)

class TicTacToeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var gameBoard by mutableStateOf(savedStateHandle.get<Array<Array<Player?>>>("gameBoard") ?: Array(3) { Array(3) { null as Player? } })
    var mcts = TicTacToeMCTS(gameBoard, Player.O)
    var currentPlayer by mutableStateOf(savedStateHandle.get<Player>("currentPlayer") ?: Player.X)
    var playerXName by mutableStateOf(savedStateHandle.get<String>("playerXName") ?: "")
    var playerOName by mutableStateOf(savedStateHandle.get<String>("playerOName") ?: "")
    var isPruningEnabled by mutableStateOf(false)
    var difficulty = Difficulty.MEDIUM

    var aiMoves = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
    var aiResourceStats = mutableMapOf<Int, MutableList<ResourceStats>>()
    var gameId by mutableStateOf(1)
    var allGamesStats = mutableListOf<GameStats>()
    var currentGameId = 1
    var modelUsed by mutableStateOf("Minimax")
    private var lastGameWinner: Player? = null

    fun saveGameState() {
        savedStateHandle.set("gameId", gameId)
        savedStateHandle.set("gameMoves", aiMoves)
        savedStateHandle.set("gameResourceStats", aiResourceStats)
        savedStateHandle.set("gameBoard", gameBoard)
        savedStateHandle.set("currentPlayer", currentPlayer)
        savedStateHandle.set("playerXName", playerXName)
        savedStateHandle.set("playerOName", playerOName)
        savedStateHandle.set("modelUsed", modelUsed)
    }

    fun restoreGameState() {
        gameId = savedStateHandle.get("gameId") ?: 0
        aiMoves = savedStateHandle.get("gameMoves") ?: mutableMapOf()
        aiResourceStats = savedStateHandle.get("gameResourceStats") ?: mutableMapOf()
        gameBoard = savedStateHandle.get("gameBoard") ?: Array(3) { Array(3) { null } }
        currentPlayer = savedStateHandle.get("currentPlayer") ?: Player.X
        playerXName = savedStateHandle.get("playerXName") ?: ""
        playerOName = savedStateHandle.get("playerOName") ?: ""
        modelUsed = savedStateHandle.get("modelUsed") ?: "MCTS"
    }

    fun getBoard(): Array<Array<Player?>> {
        return gameBoard
    }

    fun getPlayer(): Player {
        return currentPlayer
    }

    fun onSquareClick(row: Int, col: Int) {
        if (gameBoard[row][col] == null && !gameOver()) {
            gameBoard[row][col] = currentPlayer
            if (!gameOver()) {
                currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
            }
        }
        saveGameState()
        println("${hashCode()} ai moves: ${aiMoves.toList()}")
    }

    fun resetGame() {
        val currentGameMoves = aiMoves[currentGameId]?.toList() ?: emptyList()
        val currentGameResourceStats = aiResourceStats[currentGameId]?.toList() ?: emptyList()

        lastGameWinner = getWinner()

        val newGameStats = GameStats(
            gameId = currentGameId,
            moves = currentGameMoves,
            resourceStats = currentGameResourceStats,
            simulations = mcts.simulations,
            modelUsed = modelUsed,
            winRatio = calculateWinLossRatio(),
            avgDuration = calculateAvgDuration(),
            PruningEnabled = isPruningEnabled,
            difficulty = difficulty
        )
        allGamesStats.add(newGameStats)

        aiMoves.clear()
        aiResourceStats.clear()

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                gameBoard[i][j] = null
            }
        }
        currentPlayer = Player.X
        currentGameId++
        saveGameState()
        println(allGamesStats.count { game ->
            val board = Array(3) { Array(3) { null as Player? } }
            game.moves.forEachIndexed { index, (row, col) ->
                board[row][col] = if (index % 2 == 0) Player.X else Player.O
            }
            checkWinner2(board, Player.O)
        })
        println(allGamesStats.size)
        println("lastGameWinner: $lastGameWinner")
        println("Win Ratio: ${calculateWinLossRatio()}")
    }
    fun isDraw(): Boolean {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (gameBoard[i][j] == null) {
                    return false
                }
            }
        }
        return true
    }
    fun gameOver(): Boolean {
        for (i in 0 until 3) {
            if ((gameBoard[i][0] != null && gameBoard[i][0] == gameBoard[i][1] && gameBoard[i][1] == gameBoard[i][2]) ||
                (gameBoard[0][i] != null && gameBoard[0][i] == gameBoard[1][i] && gameBoard[1][i] == gameBoard[2][i])) {
                return true
            }
        }

        if ((gameBoard[0][0] != null && gameBoard[0][0] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][2]) ||
            (gameBoard[0][2] != null && gameBoard[0][2] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][0])) {
            return true
        }

        return false
    }

    fun getWinner(): Player? {
        for (i in 0 until 3) {
            if (gameBoard[i][0] != null && gameBoard[i][0] == gameBoard[i][1] && gameBoard[i][1] == gameBoard[i][2]) {
                return gameBoard[i][0]
            }
            if (gameBoard[0][i] != null && gameBoard[0][i] == gameBoard[1][i] && gameBoard[1][i] == gameBoard[2][i]) {
                return gameBoard[0][i]
            }
        }

        if (gameBoard[0][0] != null && gameBoard[0][0] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][2]) {
            return gameBoard[0][0]
        }

        if (gameBoard[0][2] != null && gameBoard[0][2] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][0]) {
            return gameBoard[0][2]
        }

        return null
    }
    //Minimax Algorithm
    fun minimaxWithoutPruning(board: Array<Array<Player?>>, depth: Int, isMaximizing: Boolean): Int {
        val searchDepth = when (difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 3
            Difficulty.HARD -> 5
        }

        if (gameOver() || depth == searchDepth) {
            return when {
                checkWinner(Player.O) -> 1
                checkWinner(Player.X) -> -1
                else -> 0
            }
        }

        val scores = mutableListOf<Int>()
        val moves = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == null) moves.add(Pair(i, j))
            }
        }
        moves.shuffle()

        for ((i, j) in moves) {
            if (board[i][j] == null) {
                board[i][j] = if (isMaximizing) Player.O else Player.X
                val score = minimaxWithoutPruning(board, depth + 1, !isMaximizing)
                board[i][j] = null
                scores.add(score)
            }
        }

        return if (isMaximizing) {
            scores.maxOrNull() ?: 0
        } else {
            scores.minOrNull() ?: 0
        }
    }

    fun minimaxWithPruning(board: Array<Array<Player?>>, depth: Int, isMaximizing: Boolean, alpha: Int, beta: Int): Int {
        val searchDepth = when (difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 3
            Difficulty.HARD -> 5
        }

        if (gameOver() || depth == searchDepth) {
            return when {
                checkWinner(Player.O) -> 1
                checkWinner(Player.X) -> -1
                else -> 0
            }
        }

        var alphaTemp = alpha
        var betaTemp = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == null) {
                        board[i][j] = Player.O
                        val eval = minimaxWithPruning(board, depth + 1, false, alphaTemp, betaTemp)
                        board[i][j] = null
                        maxEval = maxOf(maxEval, eval)
                        alphaTemp = maxOf(alphaTemp, eval)
                        if (betaTemp <= alphaTemp) break
                    }
                }
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == null) {
                        board[i][j] = Player.X
                        val eval = minimaxWithPruning(board, depth + 1, true, alphaTemp, betaTemp)
                        board[i][j] = null
                        minEval = minOf(minEval, eval)
                        betaTemp = minOf(betaTemp, eval)
                        if (betaTemp <= alphaTemp) break
                    }
                }
            }
            return minEval
        }
    }

    fun togglePruning() {
        isPruningEnabled = !isPruningEnabled
    }

    fun findBestMove(gameBoard: Array<Array<Player?>>): Pair<Int, Int>? {
        var bestScore = Int.MIN_VALUE
        var move: Pair<Int, Int>? = null

        val minimaxFunction: (Array<Array<Player?>>, Int, Boolean, Int, Int) -> Int =
            if (isPruningEnabled) ::minimaxWithPruning else { board, depth, isMaximizing, _, _ ->
                minimaxWithoutPruning(board, depth, isMaximizing)
            }

        for (i in 0..2) {
            for (j in 0..2) {
                if (gameBoard[i][j] == null) {
                    gameBoard[i][j] = Player.O
                    val score = minimaxFunction(gameBoard, 0, false, Int.MIN_VALUE, Int.MAX_VALUE)
                    gameBoard[i][j] = null

                    if (score > bestScore) {
                        bestScore = score
                        move = Pair(i, j)
                    }
                }
            }
        }

        return move
    }

    fun checkWinner(player: Player): Boolean {
        for (i in 0..2) {
            if (gameBoard[i][0] == player && gameBoard[i][1] == player && gameBoard[i][2] == player) return true
            if (gameBoard[0][i] == player && gameBoard[1][i] == player && gameBoard[2][i] == player) return true
        }
        if (gameBoard[0][0] == player && gameBoard[1][1] == player && gameBoard[2][2] == player) return true
        if (gameBoard[0][2] == player && gameBoard[1][1] == player && gameBoard[2][0] == player) return true
        return false
    }

    fun playAI(context: Context): Pair<Int, Int>? {
        val startCpuTime = getAppCpuTime(context)
        val startMemory = getAppMemoryUsage()
        val startTime = System.currentTimeMillis()

        val move = findBestMove(gameBoard)

        val endTime = System.currentTimeMillis()
        val endCpuTime = getAppCpuTime(context)
        val endMemory = getAppMemoryUsage()

        val cpuTimeUsed = (endCpuTime - startCpuTime) / 1_000_000
        val memoryUsed = endMemory - startMemory
        val timeToMove = endTime - startTime

        modelUsed = "Minimax"
        val stats = ResourceStats(cpuTimeUsed, memoryUsed, timeToMove)

        move?.let { (row, col) ->
            gameBoard = gameBoard.copyOf().apply { this[row][col] = Player.O }
            currentPlayer = Player.X

            aiMoves.getOrPut(currentGameId) { mutableListOf() }.add(move)
            aiResourceStats.getOrPut(currentGameId) { mutableListOf() }.add(stats)
            saveGameState()
        }

        return move
    }
//MCTS
    fun playAI2(context: Context): Pair<Int, Int>? {
        if (gameOver() || isDraw()) return null

        val startCpuTime = getAppCpuTime(context)
        val startMemory = getAppMemoryUsage()
        val startTime = System.currentTimeMillis()

        val endTime = System.currentTimeMillis()
        val endCpuTime = getAppCpuTime(context)
        val endMemory = getAppMemoryUsage()

        val cpuTimeUsed = (endCpuTime - startCpuTime) / 1_000_000
        val memoryUsed = endMemory - startMemory
        val timeToMove = endTime - startTime

        val stats = ResourceStats(cpuTimeUsed, memoryUsed, timeToMove)


        mcts.initialBoard = gameBoard.map { it.clone() }.toTypedArray()
        val move = mcts.findBestMove()?.let { Pair(it.row, it.col) }
        modelUsed = "MCTS"
        move?.let { (row, col) ->
            gameBoard = gameBoard.map { it.clone() }.toTypedArray().apply { this[row][col] = Player.O }
            currentPlayer = Player.X

            aiMoves.getOrPut(currentGameId) { mutableListOf() }.add(move)
            aiResourceStats.getOrPut(currentGameId) { mutableListOf() }.add(stats)
            saveGameState()
        }
    return move
}

    fun getAppCpuTime(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(processInfo)

        return Debug.threadCpuTimeNanos()
    }
    fun getAppMemoryUsage(): Int {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)

        return memoryInfo.totalPss
    }
    fun resetStats() {
        allGamesStats.clear()
    }
    fun checkWinner2(board: Array<Array<Player?>>, player: Player): Boolean {
        //  rows and columns
        for (i in 0 until 3) {
            if ((0 until 3).all { board[i][it] == player } ||
                (0 until 3).all { board[it][i] == player }) {
                return true
            }
        }
        //  diagonals
        if ((0 until 3).all { board[it][it] == player } ||
            (0 until 3).all { board[it][2 - it] == player }) {
            return true
        }
        return false
    }
    fun calculateWinLossRatio(): Float {
        if (allGamesStats.isEmpty()) return 0f

        val oWins = allGamesStats.count { game ->
            val board = Array(3) { Array(3) { null as Player? } }
            game.moves.forEachIndexed { index, (row, col) ->
                board[row][col] = if (index % 2 == 0) Player.X else Player.O
            }

            checkWinner2(board, Player.O)
        }

        return oWins.toFloat() / allGamesStats.size
    }


    fun calculateAvgDuration(): Long {
        val totalDuration = allGamesStats.sumOf { it.resourceStats.sumOf { stat -> stat.timeToMove } }
        val totalMoves = allGamesStats.sumOf { it.moves.size }
        return if (totalMoves > 0) totalDuration / totalMoves else 0L
    }
}
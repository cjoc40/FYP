import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fyp2.GameType
import com.example.fyp2.MCTS
import com.example.fyp2.pages.Difficulty
import com.example.fyp2.pages.Player
import com.example.fyp2.pages.ResourceStats
import kotlin.math.max
import kotlin.math.min

data class GameStats2(
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

class Connect4ViewModel : ViewModel() {
    val gameBoard = Array(6) { Array<Player?>(7) { null } }
    val mcts = MCTS(gameBoard, Player.Red)
    private var currentPlayer = Player.Black
    var playerXName = ""
    var playerOName = ""
    var allGamesStats = mutableListOf<GameStats2>()
    var currentGameId = 1
    var aiMoves = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
    var aiResourceStats = mutableMapOf<Int, MutableList<ResourceStats>>()
    var difficulty = Difficulty.MEDIUM
    var isPruningEnabled by mutableStateOf(false)
    var modelUsed by mutableStateOf("MCTS")
    var lastGameWinner: Player? = null

    fun getBoard(): Array<Array<Player?>> {
        return gameBoard
    }

    fun getCurrentPlayer(): Player {
        return currentPlayer
    }

    fun onSquareClick(row: Int, col: Int) {
        if (gameBoard[row][col] == null) {
            gameBoard[row][col] = currentPlayer
            currentPlayer = if (currentPlayer == Player.Black) Player.Red else Player.Black
        }
    }
    fun resetGame() {
        val currentGameMoves = aiMoves[currentGameId]?.toList() ?: emptyList()
        val currentGameResourceStats = aiResourceStats[currentGameId]?.toList() ?: emptyList()

        lastGameWinner = if (checkWin(Player.Red, gameBoard)) {
            Player.Red
        } else if (checkWin(Player.Black, gameBoard)) {
            Player.Black
        } else {
            null
        }

        val newGameStats = GameStats2(
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
        println(newGameStats)

        aiMoves.clear()
        aiResourceStats.clear()

        for (row in gameBoard.indices) {
            for (col in gameBoard[row].indices) {
                gameBoard[row][col] = null
            }
        }
        currentPlayer = Player.Black
        currentGameId++
    }

    fun isDraw(): Boolean {
        for (i in 0 until 6) {
            for (j in 0 until 7) {
                if (gameBoard[i][j] == null) {
                    return false
                }
            }
        }
        return true
    }

    fun gameOver(): Boolean {
        //horizontal
        for (row in 0 until 6) {
            for (col in 0 until 4) {
                if (gameBoard[row][col] != null &&
                    gameBoard[row][col] == gameBoard[row][col + 1] &&
                    gameBoard[row][col] == gameBoard[row][col + 2] &&
                    gameBoard[row][col] == gameBoard[row][col + 3]
                ) {
                    return true
                }
            }
        }

        //vertical
        for (col in 0 until 7) {
            for (row in 0 until 3) {
                if (gameBoard[row][col] != null &&
                    gameBoard[row][col] == gameBoard[row + 1][col] &&
                    gameBoard[row][col] == gameBoard[row + 2][col] &&
                    gameBoard[row][col] == gameBoard[row + 3][col]
                ) {
                    return true
                }
            }
        }

        //diagonal /
        for (row in 0 until 3) {
            for (col in 0 until 4) {
                if (gameBoard[row][col] != null &&
                    gameBoard[row][col] == gameBoard[row + 1][col + 1] &&
                    gameBoard[row][col] == gameBoard[row + 2][col + 2] &&
                    gameBoard[row][col] == gameBoard[row + 3][col + 3]
                ) {
                    return true
                }
            }
        }

        //diagonal \
        for (row in 3 until 6) {
            for (col in 0 until 4) {
                if (gameBoard[row][col] != null &&
                    gameBoard[row][col] == gameBoard[row - 1][col + 1] &&
                    gameBoard[row][col] == gameBoard[row - 2][col + 2] &&
                    gameBoard[row][col] == gameBoard[row - 3][col + 3]
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun mctsMove(): Pair<Int, Int>? {
        val startCpuTime = getAppCpuTime()
        val startMemory = getAppMemoryUsage()
        val startTime = System.currentTimeMillis()

        val bestMove = mcts.findBestMove()?.let { Pair(it.row, it.col) }

        val endTime = System.currentTimeMillis()
        val endCpuTime = getAppCpuTime()
        val endMemory = getAppMemoryUsage()

        val cpuTimeUsed = (endCpuTime - startCpuTime) / 1_000_000
        val memoryUsed = endMemory - startMemory
        val timeToMove = endTime - startTime

        val stats = ResourceStats(cpuTimeUsed, memoryUsed, timeToMove)

        bestMove?.let { (row, col) ->
            gameBoard[row][col] = Player.Red

            aiMoves.getOrPut(currentGameId) { mutableListOf() }.add(bestMove)
            aiResourceStats.getOrPut(currentGameId) { mutableListOf() }.add(stats)
        }
        println("Updated stats: ${mcts.stats}")

        modelUsed = "MCTS"
        return bestMove
    }

    private fun evaluateBoard(board: Array<Array<Player?>>): Int {
        if (checkWin(Player.Red, board)) return 1000000
        if (checkWin(Player.Black, board)) return -1000000
        return 0
    }

    private fun checkWin(player: Player, board: Array<Array<Player?>>): Boolean {
        // Horizontal
        for (r in 0..5) for (c in 0..3) if (board[r][c] == player && board[r][c + 1] == player && board[r][c + 2] == player && board[r][c + 3] == player) return true
        // Vertical
        for (r in 0..2) for (c in 0..6) if (board[r][c] == player && board[r + 1][c] == player && board[r + 2][c] == player && board[r + 3][c] == player) return true
        // Diagonal \
        for (r in 0..2) for (c in 0..3) if (board[r][c] == player && board[r + 1][c + 1] == player && board[r + 2][c + 2] == player && board[r + 3][c + 3] == player) return true
        // Diagonal /
        for (r in 0..2) for (c in 3..6) if (board[r][c] == player && board[r + 1][c - 1] == player && board[r + 2][c - 2] == player && board[r + 3][c - 3] == player) return true
        return false
    }

    private fun isBoardFull(board: Array<Array<Player?>>): Boolean {
        for (row in board) {
            for (cell in row) {
                if (cell == null) return false
            }
        }
        return true
    }

    private fun minimax(board: Array<Array<Player?>>, depth: Int, isMaximizing: Boolean, alpha: Int, beta: Int): Int {
        val searchDepth = when (difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 3
            Difficulty.HARD -> 5
        }
        if (depth == searchDepth || checkWin(Player.Red, board) || checkWin(Player.Black, board) || isBoardFull(board)) {
            return evaluateBoard(board)
        }

        var alphaTemp = alpha
        var betaTemp = beta

        if (isMaximizing) {
            var bestVal = Int.MIN_VALUE
            for (col in 0..6) {
                for (row in 5 downTo 0) {
                    if (board[row][col] == null) {
                        val newBoard = board.map { it.clone() }.toTypedArray()
                        newBoard[row][col] = Player.Red
                        val value = minimax(newBoard, depth + 1, false, alphaTemp, betaTemp)
                        bestVal = max(bestVal, value)
                        alphaTemp = max(alphaTemp, value)
                        if (betaTemp <= alphaTemp) break
                        break
                    }
                }
            }
            return bestVal
        } else {
            var bestVal = Int.MAX_VALUE
            for (col in 0..6) {
                for (row in 5 downTo 0) {
                    if (board[row][col] == null) {
                        val newBoard = board.map { it.clone() }.toTypedArray()
                        newBoard[row][col] = Player.Black
                        val value = minimax(newBoard, depth + 1, true, alphaTemp, betaTemp)
                        bestVal = min(bestVal, value)
                        betaTemp = min(betaTemp, value)
                        if (betaTemp <= alphaTemp) break
                        break
                    }
                }
            }
            return bestVal
        }
    }

    fun minimaxMove(): Pair<Int, Int>? {
        var bestMove: Pair<Int, Int>? = null
        var bestValue = Int.MIN_VALUE
        val startCpuTime = getAppCpuTime()
        val startMemory = getAppMemoryUsage()
        val startTime = System.currentTimeMillis()
        for (col in 0..6) {
            for (row in 5 downTo 0) {
                if (gameBoard[row][col] == null) {
                    val newBoard = gameBoard.map { it.clone() }.toTypedArray()
                    newBoard[row][col] = Player.Red
                    val moveValue = minimax(newBoard, 0, false, Int.MIN_VALUE, Int.MAX_VALUE)
                    newBoard[row][col] = null
                    if (moveValue > bestValue) {
                        bestValue = moveValue
                        bestMove = Pair(row, col)
                    }
                    break
                }
            }
        }
        val endTime = System.currentTimeMillis()
        val endCpuTime = getAppCpuTime()
        val endMemory = getAppMemoryUsage()

        val cpuTimeUsed = (endCpuTime - startCpuTime) / 1_000_000
        val memoryUsed = endMemory - startMemory
        val timeToMove = endTime - startTime

        val stats = ResourceStats(cpuTimeUsed, memoryUsed, timeToMove)
        bestMove?.let {
            aiMoves.getOrPut(currentGameId) { mutableListOf() }.add(it)
            aiResourceStats.getOrPut(currentGameId) { mutableListOf() }.add(stats)
        }
        modelUsed = "Minimax"
        return bestMove
    }
    fun togglePruning() {
        isPruningEnabled = !isPruningEnabled
    }

    fun getAppCpuTime(): Long {
        return Debug.threadCpuTimeNanos()
    }

    fun getAppMemoryUsage(): Int {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).toInt() / 1024
    }
    fun resetStats() {
        allGamesStats.clear()
        aiMoves.clear()
        aiResourceStats.clear()
        currentGameId = 1
    }

    fun calculateWinLossRatio(): Float {
        val totalGames = allGamesStats.size
        val wins = allGamesStats.count { gameStats ->
            gameStats.moves.lastOrNull()?.let { move ->
                lastGameWinner == Player.Red
            } == true
        }
        return if (totalGames > 0) wins.toFloat() / totalGames else 0.0f
    }

    fun calculateAvgDuration(): Long {
        val totalDuration = allGamesStats.sumOf { it.resourceStats.sumOf { stat -> stat.timeToMove } }
        val totalMoves = allGamesStats.sumOf { it.moves.size }
        return if (totalMoves > 0) totalDuration / totalMoves else 0L
    }
}
package com.example.fyp2

import android.os.Debug
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fyp2.pages.Difficulty
import com.example.fyp2.pages.Player
import kotlin.math.ln
import kotlin.math.sqrt

data class Move(val row: Int, val col: Int)
enum class GameType { CONNECT4, TICTACTOE }

data class Stats(
    var totalTimeToMove: Long = 0,
    var totalCpuUsage: Long = 0,
    var totalMemoryUsed: Int = 0
)

data class ResourceStats2(
    val cpuUsage: Long,
    val memoryUsed: Int,
    val timeToMove: Long
)

class MCTS(var initialBoard: Array<Array<Player?>>, private val aiPlayer: Player) {
    val stats = Stats()
    var simulations by mutableStateOf(3000)

    data class Node(
        val board: Array<Array<Player?>>,
        val parent: Node? = null,
        val move: Move? = null,
        var wins: Int = 0,
        var visits: Int = 0,
        val children: MutableList<Node> = mutableListOf()
    )

    private fun cloneBoard(board: Array<Array<Player?>>): Array<Array<Player?>> =
        Array(board.size) { row -> board[row].copyOf() }

    fun findBestMove(): Move? {
        val rootNode = Node(board = cloneBoard(initialBoard))
        val gameStats = GameStats(1, mutableListOf(), mutableListOf(), simulations, false, Difficulty.EASY, "MCTS")
        repeat(simulations) {
            val resourceStats = simulate(rootNode)
            stats.totalTimeToMove += resourceStats.timeToMove
            stats.totalCpuUsage += resourceStats.cpuUsage
            stats.totalMemoryUsed += resourceStats.memoryUsed
        }
        val bestMove = rootNode.children.maxByOrNull { it.visits }?.move

        gameStats.simulations = simulations
        return bestMove
    }

    private fun simulate(node: Node): ResourceStats2 {
        var current = node
        var board = cloneBoard(current.board)
        val parent = current.parent
        var currentPlayer = if (parent == null) aiPlayer else if (parent.board.count { it.contains(null) } % 2 == 0) Player.Black else Player.Red

        // Selection
        while (current.children.isNotEmpty()) {
            current = current.children.maxByOrNull { uct(it, node.visits) }!!
            board = cloneBoard(current.board)
            currentPlayer = if (currentPlayer == Player.Black) Player.Red else Player.Black
        }

        // Expansion
        val availableMoves = getAvailableMoves(board)
        if (availableMoves.isEmpty() || isGameOver(board)) {
            backpropagate(current, getResult(board, aiPlayer))
            return ResourceStats2(0, 0, 0)
        }

        availableMoves.forEach { move ->
            val newBoard = cloneBoard(board)
            makeMove(newBoard, move, currentPlayer)
            val childNode = Node(board = newBoard, parent = current, move = move)
            current.children.add(childNode)
        }
        val randomChild = current.children.random()
        board = cloneBoard(randomChild.board)
        currentPlayer = if (currentPlayer == Player.Black) Player.Red else Player.Black
        current = randomChild

        // Simulation
        val startTime = System.nanoTime()
        val initialCpuUsage = getCpuUsage()
        val initialMemoryUsage = getMemoryUsage()

        while (!isGameOver(board)) {
            val moves = getAvailableMoves(board)
            if (moves.isEmpty()) break
            val move = moves.random()
            makeMove(board, move, currentPlayer)
            currentPlayer = if (currentPlayer == Player.Black) Player.Red else Player.Black
        }

        val timeTaken = System.nanoTime() - startTime
        val cpuUsage = getCpuUsage() - initialCpuUsage
        val memoryUsed = getMemoryUsage() - initialMemoryUsage

        backpropagate(current, getResult(board, aiPlayer))

        return ResourceStats2(cpuUsage, memoryUsed, (timeTaken / 1_000_000))
    }

    private fun backpropagate(node: Node, result: Int) {
        var current: Node? = node
        while (current != null) {
            current.visits++
            current.wins += result
            current = current.parent
        }
    }

    private fun uct(node: Node, totalVisits: Int): Double {
        if (node.visits == 0) return Double.MAX_VALUE
        return (node.wins.toDouble() / node.visits) + (2.0 * sqrt(ln(totalVisits.toDouble()) / node.visits))
    }

    private fun getAvailableMoves(board: Array<Array<Player?>>): List<Move> =
        (0 until 7).mapNotNull { col ->
            (5 downTo 0).firstOrNull { board[it][col] == null }?.let { Move(it, col) }
        }

    private fun makeMove(board: Array<Array<Player?>>, move: Move, player: Player) {
        board[move.row][move.col] = player
    }

    private fun isGameOver(board: Array<Array<Player?>>): Boolean =
        getAvailableMoves(board).isEmpty() || hasWinner(board)

    private fun hasWinner(board: Array<Array<Player?>>): Boolean {
        //Horizontal
        for (r in 0..5) for (c in 0..3) if (board[r][c] != null && (0..3).all { board[r][c + it] == board[r][c] }) return true
        //Vertical
        for (c in 0..6) for (r in 0..2) if (board[r][c] != null && (0..3).all { board[r + it][c] == board[r][c] }) return true
        //Diagonal \
        for (r in 0..2) for (c in 0..3) if (board[r][c] != null && (0..3).all { board[r + it][c + it] == board[r][c] }) return true
        //Diagonal /
        for (r in 0..2) for (c in 3..6) if (board[r][c] != null && (0..3).all { board[r + it][c - it] == board[r][c] }) return true
        return false
    }

    private fun getResult(board: Array<Array<Player?>>, aiPlayer: Player): Int =
        if (hasWinner(board)) if (findWinner(board) == aiPlayer) 1 else -1 else 0

    private fun findWinner(board: Array<Array<Player?>>): Player? {
        //Horizontal
        for (r in 0..5) for (c in 0..3) if (board[r][c] != null && (0..3).all { board[r][c + it] == board[r][c] }) return board[r][c]
        //Vertical
        for (c in 0..6) for (r in 0..2) if (board[r][c] != null && (0..3).all { board[r + it][c] == board[r][c] }) return board[r][c]
        //Diagonal \
        for (r in 0..2) for (c in 0..3) if (board[r][c] != null && (0..3).all { board[r + it][c + it] == board[r][c] }) return board[r][c]
        //Diagonal /
        for (r in 0..2) for (c in 3..6) if (board[r][c] != null && (0..3).all { board[r + it][c - it] == board[r][c] }) return board[r][c]
        return null
    }

    private fun getCpuUsage(): Long = Debug.threadCpuTimeNanos()

    private fun getMemoryUsage(): Int =
        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).toInt() / 1024
}
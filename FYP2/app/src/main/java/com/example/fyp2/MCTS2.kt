package com.example.fyp2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fyp2.pages.Player
import kotlin.math.ln
import kotlin.math.sqrt

class TicTacToeMCTS(var initialBoard: Array<Array<Player?>>, private val aiPlayer: Player) {
    var simulations by mutableStateOf(3000)

    private data class Node(
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

        repeat(simulations) {
            simulate(rootNode)
        }

        val bestMove = rootNode.children.maxByOrNull { it.visits }?.move
        println("$bestMove ${rootNode.children.map { it.visits }}")
        return bestMove
    }

    private fun simulate(node: Node) {
        var current = node
        var board = cloneBoard(current.board)
        var currentPlayer = if (current.parent == null) aiPlayer else getOpponent(getPlayerFromMove(current))

        // Selection
        while (current.children.isNotEmpty()) {
            current = current.children.maxByOrNull { uct(it, node.visits) }!!
            board = cloneBoard(current.board)
            currentPlayer = getOpponent(currentPlayer)
        }

        // Expansion
        if (isGameOver(board)) {
            backpropagate(current, getResult(board))
            return
        }

        val availableMoves = getAvailableMoves(board)
        availableMoves.forEach { move ->
            val newBoard = cloneBoard(board)
            makeMove(newBoard, move, currentPlayer)
            current.children.add(Node(newBoard, current, move))
        }

        if (current.children.isEmpty()) return

        val randomChild = current.children.random()
        board = cloneBoard(randomChild.board)
        current = randomChild
        currentPlayer = getOpponent(currentPlayer)

        while (!isGameOver(board)) {
            val moves = getAvailableMoves(board)
            if (moves.isEmpty()) break

            currentPlayer = getOpponent(currentPlayer)

            val winningMove = moves.find { move ->
                val testBoard = board.map { it.copyOf() }.toTypedArray()
                makeMove(testBoard, move, currentPlayer)
                hasWinner(testBoard)
            }
            val move = winningMove ?: moves.random()

            makeMove(board, move, currentPlayer)

        }

        backpropagate(current, getResult(board))
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
        return (node.wins.toDouble() / node.visits) + (2.0 * sqrt(ln(totalVisits.toDouble()) / totalVisits))
    }

    private fun getAvailableMoves(board: Array<Array<Player?>>): List<Move> {
        val moves = mutableListOf<Move>()
        for (row in 0..2) {
            for (col in 0..2) {
                if (board[row][col] == null) {
                    moves.add(Move(row, col))
                }
            }
        }
        return moves
    }

    private fun makeMove(board: Array<Array<Player?>>, move: Move, player: Player) {
        board[move.row][move.col] = player
    }

    private fun isGameOver(board: Array<Array<Player?>>): Boolean {
        return hasWinner(board) || getAvailableMoves(board).isEmpty()
    }

    private fun hasWinner(board: Array<Array<Player?>>): Boolean {
        for (i in 0..2) {
            if ((board[i][0] != null && board[i][0] == board[i][1] && board[i][0] == board[i][2]) ||
                (board[0][i] != null && board[0][i] == board[1][i] && board[0][i] == board[2][i])) {
                return true
            }
        }
        return (board[0][0] != null && board[0][0] == board[1][1] && board[0][0] == board[2][2]) ||
                (board[0][2] != null && board[0][2] == board[1][1] && board[0][2] == board[2][0])
    }

    private fun getResult(board: Array<Array<Player?>>): Int {
        val winner = findWinner(board)
        return when (winner) {
            aiPlayer -> 1
            null -> 0
            else -> -1
        }
    }

    private fun findWinner(board: Array<Array<Player?>>): Player? {
        for (i in 0..2) {
            if (board[i][0] != null && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return board[i][0]
            }
            if (board[0][i] != null && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                return board[0][i]
            }
        }
        if (board[0][0] != null && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return board[0][0]
        }
        if (board[0][2] != null && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            return board[0][2]
        }
        return null
    }

    private fun getOpponent(player: Player): Player = if (player == Player.X) Player.O else Player.X

    private fun getPlayerFromMove(node: Node): Player {
        val parent = node.parent!!
        val move = parent.move!!
        return parent.board[move.row][move.col]!!
    }
}
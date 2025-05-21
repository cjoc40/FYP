**Project Overview**
This project is a Kotlin-based application for using the algorithms Minimax and Monte Carlo Tree Search (MCTS) for AI decision-making comparisons between Tic-Tac-Toe and Connect4.  

**Key Features**
1.     AI Algorithms: Minimax with optional pruning and MCTS(Monte-Carlo tree search).
2.     Game Modes: Tic-Tac-Toe and Connect4.
3.     Performance Stats eg. CPU usage, memory usage, and move duration.
4.     Firebase Integration for user authentication and game data storage(Future work).

**Notes**
*     **Tic-Tac-Toe difficulty levels are in reverse order: Hard, Medium, Easy.**
*     Higher difficulty levels for MCTS on Connect4 can cause bugs or app crashes/hangs.
*     Statistics of a played match are only saved once a new game is started.
*     The difference of Minimax and MCTS stats can lead to Minimax comparison bars being hard to see.
*     Menu buttons from left to right: Account, Home and Statistics.
*     Minimax has a pruning option that can be toggled on and off which reduces the number of nodes evaluated i.e. the time taken to make a move.

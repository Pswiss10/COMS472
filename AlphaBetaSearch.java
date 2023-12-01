package edu.iastate.cs472.proj2;

/**
 * 
 * @author Peter Wissman
 *
 */


/**
 * This class implements the Alpha-Beta pruning algorithm to find the best 
 * move at current state.
*/
public class AlphaBetaSearch extends AdversarialSearch {

    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     * 
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king

        //System.out.println(board);
        //System.out.println();

        CheckersMove bestMove = alphaBetaSearch(legalMoves);
        return bestMove;
    }



    public CheckersMove alphaBetaSearch(CheckersMove[] moves) {
        CheckersMove result = null;
        double resultValue = Double.NEGATIVE_INFINITY;
        int player = CheckersData.BLACK;
        int depth = 10;
        for (CheckersMove action : moves) {
            double value = minValue(board, action, player, resultValue, Double.POSITIVE_INFINITY, depth);
            if (value > resultValue) {
                result = action;
                resultValue = value;
            }
        }
        return result;
    }

    public double maxValue(CheckersData state, CheckersMove thisAction, int player, double alpha, double beta, int depth) {
        CheckersData newState = simulateMove(state, thisAction);
        int newPlayer = (player == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
        if (isTerminal(newState) || depth <= 0)
            return (double) evaluateBoard(newState);
    
        double value = Double.NEGATIVE_INFINITY;
        for (CheckersMove action : getActions(newState, newPlayer)) {
            value = Math.max(value, minValue(newState, action, newPlayer, alpha, beta, depth - 1));
            if (value >= beta)
                return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }
    
    public double minValue(CheckersData state, CheckersMove thisAction, int player, double alpha, double beta, int depth) {
        CheckersData newState = simulateMove(state, thisAction);
        int newPlayer = (player == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;

        if (isTerminal(newState) || depth <= 0)
            return (double) evaluateBoard(newState);
    
        double value = Double.POSITIVE_INFINITY;
        for (CheckersMove action : getActions(newState, newPlayer)) {
            value = Math.min(value, maxValue(newState, action, newPlayer, alpha, beta, depth - 1));
            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
        return value;
    }

    public CheckersData simulateMove(CheckersData currentState, CheckersMove move) {
        CheckersData newState = currentState.clone();
        newState.makeMove(move);
        return newState;
    }

    private boolean isTerminal(CheckersData state) {
        return evaluateBoard(state) != 0;
    }

    private CheckersMove[] getActions(CheckersData state, int player) {
        return state.getLegalMoves(player);
    }

/**
 * Evaluate the current state of the game.
 *
 * @param board Current state of the game.
 * @return Evaluation value:
 *         1 if the agent wins,
 *         -1 if the agent loses,
 *         0 if it's a draw.
 */
private int evaluateBoard(CheckersData state) {
    int winner = state.getWinner();

    if (winner == CheckersData.RED) {
        return -1; // Agent (black) loses, so evaluation is -1.
    } else if (winner == CheckersData.BLACK) {
        return 1; // Agent (black) wins, so evaluation is 1.
    } else {
        return 0; // It's a draw, so evaluation is 0.
    }
}
}

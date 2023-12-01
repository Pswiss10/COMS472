package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author Peter Wissman
 *
 */

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {


    public static final double UCB_CONSTANT = Math.sqrt(2);

    private final int totalIterations = 2000;
    private int iterations = totalIterations;
    private MCTree<CheckersData> tree;

    public MonteCarloTreeSearch() {
        tree = new MCTree<>(this);
    }

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


    @Override
    public CheckersMove makeMove(CheckersMove[] legalMoves) {

        System.out.println(board);
        System.out.println();
        
        return makeDecision(board);
    }


    public CheckersMove makeDecision(CheckersData state) {

        tree.addRoot(state);

        while (iterations > 0) {
            
            MCNode<CheckersData> leaf = select(tree);
        
            MCNode<CheckersData> child = expand(leaf);

            boolean result = simulate(child);

            backpropagate(result, child);
            --iterations;
        }
        iterations = totalIterations;
        return bestAction(tree.getRoot());
    }

    private MCNode<CheckersData> select(MCTree<CheckersData> gameTree) {
        MCNode<CheckersData> node = gameTree.getRoot();
        while (!isTerminal(node.getState()) && isNodeFullyExpanded(node)) {
            node = gameTree.getChildWithMaxUCT(node);
        }
        return node;
    }

    private MCNode<CheckersData> expand(MCNode<CheckersData> leaf) {
        if (isTerminal(leaf.getState())) return leaf;
        else {
            MCNode<CheckersData> child = randomlySelectUnvisitedChild(leaf);
            return child;
        }
    }

    private boolean simulate(MCNode<CheckersData> node) {
        int newPlayer = CheckersData.RED;
        while (!isTerminal(node.getState())) {
            newPlayer = (newPlayer == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
            Random rand = new Random();
            List<CheckersMove> actions = convertArrayToList(node.getState().getLegalMoves(newPlayer));
            CheckersMove move = actions.get(rand.nextInt(actions.size()));
            CheckersData result = simulateMove(node.getState(), move);            
            node = new MCNode<CheckersData>(result);
        }
        return evaluateBoard(node.getState()) == 1;
    }

    private void backpropagate(boolean result, MCNode<CheckersData> node) {
        tree.updateStats(result, node);
        if (tree.getParent(node) != null) backpropagate(result, tree.getParent(node));
    }

    private CheckersMove bestAction(MCNode<CheckersData> root) {
        MCNode<CheckersData> bestChild = tree.getChildWithMaxPlayouts(root);
        List<CheckersMove> actions = convertArrayToList(root.getState().getLegalMoves(CheckersData.BLACK));
        for (CheckersMove move : actions) {
            CheckersData result = simulateMove(root.getState(), move);
            if (compareBoards(bestChild.getState().board, result.board)) {
                return move;
            }
        }
        //should never reach this
        return null;
    }

    private boolean isNodeFullyExpanded(MCNode<CheckersData> node) {
        List<CheckersData> visitedChildren = tree.getVisitedChildren(node);
        List<CheckersMove> actions = convertArrayToList(node.getState().getLegalMoves(CheckersData.BLACK));
        for (CheckersMove move : actions) {
            CheckersData result = simulateMove(node.getState(), move);
            if (!visitedChildren.contains(result)) {
                return false;
            }
        }
        return true;
    }

    private MCNode<CheckersData> randomlySelectUnvisitedChild(MCNode<CheckersData> node) {
        List<CheckersData> unvisitedChildren = new ArrayList<>();
		List<CheckersData> visitedChildren = tree.getVisitedChildren(node);
		for (CheckersMove a : node.getState().getLegalMoves(CheckersData.BLACK)) {
			CheckersData result = simulateMove(node.getState(), a);
			if (!visitedChildren.contains(result)) unvisitedChildren.add(result);
		}
		Random rand = new Random();
		MCNode<CheckersData> newChild = tree.addChild(node, unvisitedChildren.get(rand.nextInt(unvisitedChildren.size())));
		return newChild;
    }

    private boolean isTerminal(CheckersData state) {
        return evaluateBoard(state) != 0;
    }

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

    public CheckersData simulateMove(CheckersData currentState, CheckersMove move) {
        CheckersData newState = currentState.clone();
        newState.makeMove(move);
        return newState;
    }

    public List<CheckersMove> convertArrayToList(CheckersMove[] moves) {
        List<CheckersMove> moveList = new ArrayList<>();
        for (CheckersMove move : moves) {
            moveList.add(move);
        }
        return moveList;
    }

    public boolean compareBoards(int[][] firstBoard, int[][] secondBoard){

        for (int i = 0; i < firstBoard.length; i ++){
            for (int j= 0; j < firstBoard.length; j++){
                if (firstBoard[i][j] != secondBoard[i][j]){
                    return false;
                }
            }
        }
        return true;
    }
}

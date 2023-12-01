package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * 
 * @author Peter Wissman
 *
 */

/**
 * This class is to be extended by the classes AlphaBetaSearch and MonteCarloTreeSearch.
 */
public abstract class AdversarialSearch {
    protected CheckersData board;

    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    protected void setCheckersData(CheckersData board) {
        this.board = board;
    }
    
    /** 
     * 
     * @return an array of valid moves
     */
    protected CheckersMove[] legalMoves() {
    	ArrayList<CheckersMove> allMoves = new ArrayList<>();
        CheckersMove[] redBoardMoves = board.getLegalMoves(1);
        for (int i = 0; i < redBoardMoves.length; i++){
            allMoves.add(redBoardMoves[i]);
        }        
    	return allMoves.toArray(new CheckersMove[0]); 
    }
	
    /**
     * Return a move returned from either the alpha-beta search or the Monte Carlo tree search.
     * 
     * @param legalMoves
     * @return CheckersMove 
     */ 
    public abstract CheckersMove makeMove(CheckersMove[] legalMoves);
}

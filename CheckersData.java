package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 == col % 2) {
                    if (row < 3) {
                        board[row][col] = BLACK;
                    } else if (row > 4) {
                        board[row][col] = RED;
                    } else {
                        board[row][col] = EMPTY;
                    }
                } else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        int piece = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = piece;

        if (Math.abs(toRow - fromRow) == 2) {
            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
            board[jumpRow][jumpCol] = EMPTY;
        }

        if ((piece == RED && toRow == 0) || (piece == BLACK && toRow == 7)) {
            board[toRow][toCol] = (piece == RED) ? RED_KING : BLACK_KING;
        }
    }

public int getWinner() {
    boolean redPieceFound = false;
    boolean blackPieceFound = false;

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            int piece = board[row][col];
            if (piece == RED || piece == RED_KING) {
                redPieceFound = true;
            } else if (piece == BLACK || piece == BLACK_KING) {
                blackPieceFound = true;
            }
        }
    }

    boolean redHasLegalMoves = hasLegalMoves(RED);
    boolean blackHasLegalMoves = hasLegalMoves(BLACK);

    if (redPieceFound && !blackPieceFound || !blackHasLegalMoves) {
        return RED; // Red wins (black has no legal moves)
    } else if (!redPieceFound && blackPieceFound || !redHasLegalMoves) {
        return BLACK; // Black wins (red has no legal moves)
    } else {
        return EMPTY; // Game is not over
    }
}

/**
 * Check if the specified player has any legal moves.
 *
 * @param player The player to check (RED or BLACK).
 * @return true if the player has legal moves, false otherwise.
 */
private boolean hasLegalMoves(int player) {
    CheckersMove[] legalMoves = getLegalMoves(player);
    return legalMoves != null;
}

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        ArrayList<CheckersMove> moves = new ArrayList<>();
        ArrayList<CheckersMove> jumpsOnly = new ArrayList<>();
        int tempRow;
        int leftCol;
        int rightCol;
        int tempOtherRow;
        CheckersMove[] listOfJumps;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int playerColor = board[row][col];
                if (player == RED){
                    switch (playerColor){
                    case RED:
                        listOfJumps = getLegalJumpsFrom(player, row, col, false);
                            if (listOfJumps != null) {
                                for (int i = 0; i < listOfJumps.length; i++){
                                    jumpsOnly.add(listOfJumps[i]);
                                }
                            }
                        tempRow = row - 1;
                        leftCol = col - 1;
                        rightCol = col + 1;
                        if (tempRow > -1){
                            if (!(leftCol < 0)){
                                int leftCheck = board[tempRow][leftCol];
                                if (leftCheck == EMPTY) {
                                    moves.add(new CheckersMove(row, col, tempRow, leftCol));
                                }
                            }
                            if (!(rightCol > 7)){
                                int rightCheck = board[tempRow][rightCol];
                                if (rightCheck == EMPTY) {
                                    moves.add(new CheckersMove(row, col, tempRow, rightCol));
                                }
                            }
                        }
                        break;
                    case RED_KING:
                        listOfJumps = getLegalJumpsFrom(player, row, col, true);
                            if (listOfJumps != null) {
                                for (int i = 0; i < listOfJumps.length; i++){
                                    jumpsOnly.add(listOfJumps[i]);
                                }
                            }
                        tempRow = row - 1;
                        tempOtherRow = row + 1;
                        leftCol = col - 1;
                        rightCol = col +1;
                        if (tempRow > -1){
                            if (!(leftCol < 0)){
                                int leftCheck = board[tempRow][leftCol];
                                if (leftCheck == EMPTY) {
                                    moves.add(new CheckersMove(row, col, tempRow, leftCol));
                                }
                            }
                            if (!(rightCol > 7)){
                                int rightCheck = board[tempRow][rightCol];
                                if (rightCheck == EMPTY) {
                                    moves.add(new CheckersMove(row, col, tempRow, rightCol));
                                }
                            }
                        }
                        if (tempOtherRow < 8){
                            if (!(leftCol < 0)){
                                int leftCheck = board[tempOtherRow][leftCol];
                                if (leftCheck == EMPTY) {
                                    moves.add(new CheckersMove(row, col, tempOtherRow, leftCol));
                                }
                            }
                            if (!(rightCol > 7)){
                                int rightCheck = board[tempOtherRow][rightCol];
                                if (rightCheck == EMPTY) {
                                    moves.add(new CheckersMove(row, col, tempOtherRow, rightCol));
                                }
                            }
                        }
                            break;
                        default:
                            continue;
                    }
                }
                else {
                    switch (playerColor) {
                        case BLACK:
                            listOfJumps = getLegalJumpsFrom(player, row, col, false);
                            if (listOfJumps != null) {
                                for (int i = 0; i < listOfJumps.length; i++){
                                    jumpsOnly.add(listOfJumps[i]);
                                }
                            }
                            tempRow = row + 1;
                            leftCol = col - 1;
                            rightCol = col +1;
                            if (tempRow < 8){
                                if (!(leftCol < 0)){
                                    int leftCheck = board[tempRow][leftCol];
                                    if (leftCheck == EMPTY) {
                                        moves.add(new CheckersMove(row, col, tempRow, leftCol));
                                    }
                                }
                                if (!(rightCol > 7)){
                                    int rightCheck = board[tempRow][rightCol];
                                    if (rightCheck == EMPTY) {
                                        moves.add(new CheckersMove(row, col, tempRow, rightCol));
                                    }
                                }
                            }
                            break;
                        case BLACK_KING:
                            listOfJumps = getLegalJumpsFrom(player, row, col, true);
                            if (listOfJumps != null) {
                                for (int i = 0; i < listOfJumps.length; i++){
                                    jumpsOnly.add(listOfJumps[i]);
                                }
                            }
                            tempRow = row - 1;
                            tempOtherRow = row + 1;
                            leftCol = col - 1;
                            rightCol = col +1;
                            if (tempRow > -1){
                                if (!(leftCol < 0)){
                                    int leftCheck = board[tempRow][leftCol];
                                    if (leftCheck == EMPTY) {
                                        moves.add(new CheckersMove(row, col, tempRow, leftCol));
                                    }
                                }
                                if (!(rightCol > 7)){
                                    int rightCheck = board[tempRow][rightCol];
                                    if (rightCheck == EMPTY) {
                                        moves.add(new CheckersMove(row, col, tempRow, rightCol));
                                    }
                                }
                            }
                            if (tempOtherRow < 8){
                                if (!(leftCol < 0)){
                                    int leftCheck = board[tempOtherRow][leftCol];
                                    if (leftCheck == EMPTY) {
                                        moves.add(new CheckersMove(row, col, tempOtherRow, leftCol));
                                    }
                                }
                                if (!(rightCol > 7)){
                                    int rightCheck = board[tempOtherRow][rightCol];
                                    if (rightCheck == EMPTY) {
                                        moves.add(new CheckersMove(row, col, tempOtherRow, rightCol));
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        
        if (!jumpsOnly.isEmpty()) {
            return jumpsOnly.toArray(new CheckersMove[0]);
        } else {
            return moves.isEmpty() ? null : moves.toArray(new CheckersMove[0]);
        }
        
    }

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps. 
     * Each move returned in the array represents a sequence of jumps 
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col, boolean isKing) {
        ArrayList<CheckersMove> jumps = new ArrayList<>();
        Stack<JumpNode> stack = new Stack<>();
        Set<Square> visitedSquares = new HashSet<>();

        int[][] jumpDirections;
        if (player == RED) {
            jumpDirections = new int[][]{{-1, -1}, {-1, 1}};
        } else {
            jumpDirections = new int[][]{{1, -1}, {1, 1}};
        }

        stack.push(new JumpNode(row, col, null, 0));
        

        while (!stack.isEmpty()) {
            JumpNode currentNode = stack.pop();
            int currentRow = currentNode.row;
            int currentCol = currentNode.col;
            int jumpCount = currentNode.jumpCount;
            CheckersMove jumpMove = currentNode.jumpMove;

            if (jumpCount == 0) {
                jumpMove = new CheckersMove();
            }

            if (!isKing) {
                for (int[] direction : jumpDirections) {
                    int jumpRow = currentRow + direction[0];
                    int jumpCol = currentCol + direction[1];
                    int targetRow = currentRow + 2 * direction[0];
                    int targetCol = currentCol + 2 * direction[1];

                    if (isValidSquare(jumpRow, jumpCol) && isValidSquare(targetRow, targetCol)) {
                        int jumpedPiece = board[jumpRow][jumpCol];
                        int targetPiece = board[targetRow][targetCol];

                        if (isOpponentPiece(player, jumpedPiece) && targetPiece == EMPTY) {
                            if (currentCol == col && currentRow == row){
                                jumpMove = new CheckersMove(currentRow, currentCol, targetRow, targetCol);
                            } else {
                                jumpMove.addMove(targetRow, targetCol);
                            }

                            jumps.add(jumpMove);
                            stack.push(new JumpNode(targetRow, targetCol, jumpMove, jumpCount + 1));
                        }
                    }
                }
            } else {
                jumpDirections = new int[][] {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
                for (int[] direction : jumpDirections) {
                    int jumpRow = currentRow + direction[0];
                    int jumpCol = currentCol + direction[1];
                    int targetRow = currentRow + 2 * direction[0];
                    int targetCol = currentCol + 2 * direction[1];

                    if (isValidSquare(jumpRow, jumpCol) && isValidSquare(targetRow, targetCol)) {
                        int jumpedPiece = board[jumpRow][jumpCol];
                        int targetPiece = board[targetRow][targetCol];
                        Square jumpedSquare = new Square(jumpRow, jumpCol);

                        if (isOpponentPiece(player, jumpedPiece) && targetPiece == EMPTY && !visitedSquares.contains(jumpedSquare)) {
                            if (currentCol == col && currentRow == row){
                                jumpMove = new CheckersMove(currentRow, currentCol, targetRow, targetCol);
                            } else {
                                jumpMove.addMove(targetRow, targetCol);
                            }
                            visitedSquares.add(jumpedSquare);
                            stack.push(new JumpNode(targetRow, targetCol, jumpMove, jumpCount + 1));
                        }
                    }
                }
            }

            if (stack.isEmpty() && currentNode.jumpMove != null) {
                jumps.add(currentNode.jumpMove);
            }

            if (jumpCount == 0 && currentNode.jumpMove != null) {
                jumps.add(currentNode.jumpMove);
            }
        }

        return jumps.isEmpty() ? null : jumps.toArray(new CheckersMove[0]);
    }

    // Helper class to represent a node in the jump sequence
    private static class JumpNode {
        int row;
        int col;
        CheckersMove jumpMove;
        int jumpCount;

        JumpNode(int row, int col, CheckersMove jumpMove, int jumpCount) {
            this.row = row;
            this.col = col;
            this.jumpMove = jumpMove;
            this.jumpCount = jumpCount;
        }
    }

    private static class Square {
        int row;
        int col;

        Square(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Square square = (Square) obj;
            return row == square.row && col == square.col;
        }
    }

    private boolean isValidSquare(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean isOpponentPiece(int currentPlayer, int piece) {
        return (currentPlayer == RED && (piece == BLACK || piece == BLACK_KING)) ||
                (currentPlayer == BLACK && (piece == RED || piece == RED_KING));
    } 

    @Override
    public CheckersData clone() {
        CheckersData clonedData = new CheckersData();
        for (int i = 0; i < board.length; i++) {
            clonedData.board[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return clonedData;
    }
}

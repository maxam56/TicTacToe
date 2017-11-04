
public class TicTacToe {


    private char[][] board;
    public final char S_MARK = 'o';
    public final char C_MARK = 'x';
    private final char MY_MARK;
    private final char OTHER_MARK;
			
    public TicTacToe(char mark) {
        board = new char[3][3];
        MY_MARK = mark;
        if (mark == S_MARK) OTHER_MARK = C_MARK;
        else OTHER_MARK = S_MARK;
        initializeBoard();
    }
	
	
    // Set/Reset the board back to all empty values.
    public void initializeBoard() {
		
        // Loop through rows
        for (int i = 0; i < 3; i++) {
			
            // Loop through columns
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }
	
	public String getBoardString()
	{
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++)
			{
				b.append(board[i][j]);
			}
		}
		return b.toString();
	}
    // Print the current board (may be replaced by GUI implementation later)
    public void printBoard() {
        System.out.println("-------------");
		
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }
	
	public boolean isBoardFull() {
		return isBoardFull(board);
	}
    // Loop through all cells of the board and if one is found to be empty (contains char '-') then return false.
    // Otherwise the board is full.
    public boolean isBoardFull(char[][] board) {
        boolean isFull = true;
		
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    isFull = false;
                }
            }
        }
		
        return isFull;
    }
	
	public char checkForWin() {
		return checkForWin(board);
	}
	
	public int[] makeMove() {
		int best, result;
		int[] move = {0, 0};
		best = -2;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (board[row][col] == '-') {
					board[row][col] = MY_MARK;
					result = min(board);
					if (result > best) {
						best = result;
						move[0] = row;
						move[1] = col;
					}
					board[row][col] = '-';
				}
			}
		}
		return move;
		
	}
	
	private int min (char[][] board) {
		int bestResult, result;
		char winner = checkForWin(board);
		bestResult = 2;
		if (winner == MY_MARK) {
			return 1;
		} else if (winner == OTHER_MARK) {
			return -1;
		} else if (isBoardFull(board)) {
			return 0;
		}
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				if (board[r][c] == '-') {
					board[r][c] = OTHER_MARK;
					result = max(board);
					if (result < bestResult) {
						bestResult = result;
					}
					board[r][c] = '-';
				}
			}
		}
		return bestResult;
	}
	
	private int max (char[][] board) {
		
		int bestResult, result;
		char winner = checkForWin(board);
		bestResult = -2;
		//Did we win
		if (winner == MY_MARK) {
			return 1;
		} else if (winner == OTHER_MARK) {
			return -1;
		} else if (isBoardFull(board)) {
			return 0;
		}

		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				if (board[r][c] == '-') {
					board[r][c] = MY_MARK;
					result = min(board);
					if (result > bestResult) {
						bestResult = result;
					}
					board[r][c] = '-';
				}
			}
		}
		return bestResult;
	}
	
    // Returns true if there is a win, false otherwise.
    // This calls our other win check functions to check the entire board.
    public char checkForWin(char[][] board) {
    	char w1, w2, w3;
    	w1 = checkRowsForWin(board);
    	w2 = checkColumnsForWin(board);
    	w3 = checkDiagonalsForWin(board);
    	if (w1 != 'n') return w1;
    	else if (w2 != 'n') return w2;
    	else if (w3 != 'n') return w3;
    	else return 'n';
    }
	
	
    // Loop through rows and see if any are winners.
    private char checkRowsForWin(char[][] board) {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[i][0], board[i][1], board[i][2]) != 'n') {
                return board[i][0];
            }
        }
        return 'n';
    }
	
	
    // Loop through columns and see if any are winners.
    private char checkColumnsForWin(char[][] board) {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[0][i], board[1][i], board[2][i]) != 'n') {
                return board[0][i];
            }
        }
        return 'n';
    }
	
	
    // Check the two diagonals to see if either is a win. Return true if either wins.
    private char checkDiagonalsForWin(char[][] board) {
    	char d1, d2;
    	d1 = checkRowCol(board[0][0], board[1][1], board[2][2]);
    	d2 = checkRowCol(board[0][2], board[1][1], board[2][0]);
        if (d1 != 'n') return d1;
        if (d2 != 'n') return d2;
        return 'n';
    }
	
	
    // Check to see if all three values are the same (and not empty) indicating a win.
    private char checkRowCol(char c1, char c2, char c3) {
        if (((c1 != '-') && (c1 == c2) && (c2 == c3))) {
        	return c1;
        }
        return 'n';
    }
	
    // Places a mark at the cell specified by row and col with the mark of the current player.
    public boolean placeMark(char p, int row, int col) {
		
        // Make sure that row and column are in bounds of the board.
        if ((row >= 0) && (row < 3)) {
            if ((col >= 0) && (col < 3)) {
                if (board[row][col] == '-') {
                    board[row][col] = p;
                    return true;
                }
            }
        }
		
        return false;
    }
    
    public char getMark(int row, int col) {
    	return board[row][col];
    }
}

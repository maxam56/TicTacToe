import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;


public class Client {

	private Socket socket;	
	TicTacToe game;
	PrintWriter serverWriter;
	BufferedReader serverReader;
	private final char MARK = 'x';
	
	public Client(String host, int port) {
		try {
			game = new TicTacToe(MARK);
			socket = new Socket(host, port);
		} catch(IOException e) {
			System.err.println("Failed to open " + host + ":" + port);
			e.printStackTrace();
		}
		connect();
		System.out.println("Game over!");
	}
	
	private void connect() {
		boolean first = true;
		try {
			serverWriter = new PrintWriter(socket.getOutputStream(), true);
			serverReader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
			String move = serverReader.readLine();
			while (true) {
				if (first) {
					Random r = new Random();
					game.placeMark(game.C_MARK, r.nextInt(3), r.nextInt(3));
					first = false;
				} else makeMove();
				game.printBoard();
				serverWriter.println(game.getBoardString());
				move = serverReader.readLine();
				if (move.contains("win") || move.contains("loss") || move.contains("tie")) {
					System.out.println("Game over: " + move);
					game.printBoard();
					return;
				}
				fillBoard(move);
				game.printBoard();
			}
		} catch(IOException e) {
			System.err.println("Server worker: Could no open client output/output stream.");
			e.printStackTrace();
		}
		
	}
	
	private boolean fillBoard(String move) {
		if (move.length() < 9) return false;
		System.out.println(move);
		int row, col, bIdx;
		bIdx = 0;
		//Update board with client move
		for (int i = 0; i < move.length(); i++)
		{
			if (move.charAt(i) != '-' && move.charAt(i) != game.S_MARK && move.charAt(i) != game.C_MARK) {
				continue;
			}
			row = bIdx/3;
			col = bIdx%3;
			//Fill board with opponents moves, server always 'x'
			if (move.charAt(i) == game.S_MARK) {
				if (game.getMark(row, col) == '-') {
					game.placeMark(game.S_MARK, row, col);
					System.out.println("Marked at " + row + " " + col);
				}
			}
			bIdx++;

		}
		return true;
	}
	
	private void makeMove() {
		int[] move = game.makeMove();
		game.placeMark(game.C_MARK, move[0], move[1]);
	}
	
	public static void main(String[] args) {
		Client client = new Client(args[0], Integer.valueOf(args[1]));
	}
}

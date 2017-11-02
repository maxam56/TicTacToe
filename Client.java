import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {

	private Socket socket;	
	TicTacToe game;
	PrintWriter serverWriter;
	BufferedReader serverReader;
	
	public Client(String host, int port) {
		try {
			game = new TicTacToe();
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
					first = false;
					game.placeMark(1, 1);
				} else makeMove();
				
				serverWriter.println(game.getBoardString());
				move = serverReader.readLine();
				if (move.equalsIgnoreCase("win") || move.equalsIgnoreCase("loss") || move.equalsIgnoreCase("tie")) {
					System.out.println("Game over: " + move);
					game.printBoard();
					return;
				}
				fillBoard(move);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch(IOException e) {
			System.err.println("Server worker: Could no open client output/output stream.");
			e.printStackTrace();
		}
		
	}
	
	private boolean fillBoard(String move) {
		if (move.length() != 9) return false;
		int row, col;
		game.changePlayer();
		//Update board with client move
		for (int i = 0; i < move.length(); i++) {
			row = i/3;
			col = i%3;
			//Fill board with opponents moves, server always 'x'
			if (move.charAt(i) == 'x') {
				System.out.println("Found mark");
				if (game.getMark(row, col) == '-' || game.getMark(row, col) == 'x') {
					game.placeMark(row, col);
					game.printBoard();
				} else return false;
			}
		}
		game.changePlayer();
		return true;
	}
	
	private void makeMove() {
		String b = game.getBoardString();
		int row, col;
		for (int i = 0; i < b.length(); i++) {
			row = i/3;
			col = i%3;
			if (b.charAt(i) == '-') {
				game.placeMark(row, col);
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client("localhost", 1234);
	}
}

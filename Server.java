import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
public class Server {
	
	private class ServWorker extends Thread {
		private Socket client;
		private PrintWriter clientWriter;
		private BufferedReader clientReader;
		private final Server server;
		private TicTacToe game;
		final private String WIN_CODE = "win";
		final private String LOSS_CODE = "loss";
		final private String TIE_CODE = "tie";
		
		public ServWorker(final Socket client, final Server server) {
			game = new TicTacToe();
			this.client = client;
			this.server = server;
			try {
				clientWriter = new PrintWriter(this.client.getOutputStream(), true);
				clientReader = new BufferedReader(new InputStreamReader((this.client.getInputStream())));
			} catch(IOException e) {
				System.err.println("Server worker: Could no open client output/output stream.");
				e.printStackTrace();
			}
		}
		
		public void run() {
			//Print empty board to prompt clients first move
			clientWriter.println("---------");
			while (true) {
				try {
					
					//Wait for first move from client
					if (fillBoard(clientReader.readLine())) {
						//successful line read
					} else {
						clientWriter.println("Invalid move submission");
						continue;
					}
					//Check for win
					if (game.checkForWin()) {
						clientWriter.println(WIN_CODE);
						server.closeWorker(this);
						return;
					} else if (game.isBoardFull()) {
						clientWriter.println(TIE_CODE);
						server.closeWorker(this);
						return;
					} else {
						game.changePlayer();
						makeMove();
						if (game.checkForWin()) {
							clientWriter.println(LOSS_CODE);
							server.closeWorker(this);
							return;
						}
					}
					game.changePlayer(); //Change back to client mark
					//Make own move
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
		
		private void makeMove() {
			String b = game.getBoardString();
			int row, col;
			for (int i = 0; i < b.length(); i++) {
				row = i/3;
				col = i%3;
				if (b.charAt(i) == '-') {
					game.placeMark(row, col);
				}
			}
		}
		
		private boolean fillBoard(String move) {
			if (move.length() != 9) return false;
			int row, col;
			int count = 0;
			//Update board with client move
			for (int i = 0; i < move.length(); i++) {
				if (count > 1) {
					clientWriter.println("Too many moves!");
					return false;
				}
				row = i/3;
				col = i%3;
				if (move.charAt(i) == 'x' || move.charAt(i) == 'o') {
					System.out.println("Found mark");
					game.placeMark(row, col);
					game.printBoard();
					
				}
			}
			
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < 9; i++) {
				row = i/3;
				col = i%3;
				if (col == 2 && row != 2) {
					b.append(game.getMark(row, col));
					b.append("|");
				} else b.append(game.getMark(row, col));
				
				
				
			}
			clientWriter.println(b.toString());
			
			return true;
		}

	}
	private ServerSocket socket;
	
	public Server(int portNum) {
		try {
			socket = new ServerSocket(portNum);
		} catch(IOException e) {
			System.err.println("Failed to open socket on port " + portNum);
			e.printStackTrace();
		}
		waitForConnection(portNum);
	}
	
	public void closeWorker(ServWorker worker) {
		try {
			worker.join();
			System.out.println("Successfully terminated worker.");
		} catch (InterruptedException e) {
			System.err.println("Failed to join worker thread");
			e.printStackTrace();
		}
	}
	
	private void waitForConnection(int port) {
		String host = "";
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				System.out.println("Server (" + host + ") wating for connection on port: " + port + ".");
				Socket client = socket.accept();
				ServWorker worker = new ServWorker(client, this);
				worker.start();
				System.out.println("**********New Connection***********");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

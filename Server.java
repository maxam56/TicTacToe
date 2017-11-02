import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
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
		
		private volatile boolean stop = false;
		
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
		
		public void setStop() {
			stop = true;
		}
		
		public void run() {
			//Print empty board to prompt clients first move
			clientWriter.println("---------");
			while (!stop) {
				try {
					
					//Wait for first move from client
					if (!fillBoard(clientReader.readLine())) {
						//Bad move from client
						clientWriter.println("Invalid move submission");
						clientWriter.println(game.getBoardString());
						continue;
					}
					//Check for win
					if (game.checkForWin()) {
						clientWriter.println(WIN_CODE);
						server.closeWorker(this);
					} else if (game.isBoardFull()) {
						clientWriter.println(TIE_CODE);
						server.closeWorker(this);
					} else {
						game.changePlayer();
						makeMove();
						if (game.checkForWin()) {
							clientWriter.println(LOSS_CODE);
							server.closeWorker(this);
						}
					}
					clientWriter.println(game.getBoardString());
					game.changePlayer(); //Change back to client mark
					//Make own move
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
			System.out.println("Worker exiting...");
			return;
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
				//Fill board with opponents moves, client always 'o'
				if (move.charAt(i) == 'o') {
					System.out.println("Found mark");
					if (game.getMark(row, col) == '-' || game.getMark(row, col) == 'o') {
						game.placeMark(row, col);
						game.printBoard();
					} else return false;
					
					
				}
			}
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
		worker.setStop();
		System.out.println("Stopped worker.");
	}
	
	private void waitForConnection(int port) {
		String host = "";
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Server (" + host + ") wating for connection on port: " + port + ".");
			Socket client = socket.accept();
			ServWorker worker = new ServWorker(client, this);
			worker.start();
			try {
				worker.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Server exiting...");
		
	}
	public static void main(String[] args) {
		Server s = new Server(1234);
	}
}

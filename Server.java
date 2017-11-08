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
		private final char MARK = 'o';
		
		
		private volatile boolean stop = false;
		
		public ServWorker(final Socket client, final Server server) {
			game = new TicTacToe(MARK);
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
			char winner;
			while (!stop) {
				try {
					
					//Wait for first move from client
					if (!fillBoard(clientReader.readLine())) {
						//Bad move from client
						clientWriter.println("Invalid move submission");
						clientWriter.println(game.getBoardString());
						continue;
					}
					game.printBoard();
					winner = game.checkForWin();
					//Check for client win or draw
					if (winner == game.C_MARK) {
						clientWriter.println(WIN_CODE);
						server.closeWorker(this);
						continue;
					} else if (game.isBoardFull()) {
						clientWriter.println(TIE_CODE);
						server.closeWorker(this);
						continue;
						
					}
					//Make own move
					makeMove();
					game.printBoard();
					if (game.checkForWin() == game.S_MARK) {
						clientWriter.println(LOSS_CODE);
						server.closeWorker(this);
						continue;
					}
					clientWriter.println(game.getBoardString());
					
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
			}
			System.out.println("Worker exiting...");
			return;
		}
		
		private void makeMove() {
			int[] move = game.makeMove();
			game.placeMark(game.S_MARK, move[0], move[1]);
			
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
				if (move.charAt(i) == game.C_MARK) {
					if (game.getMark(row, col) == '-') {
						game.placeMark(game.C_MARK, row, col);
						System.out.println("Marked at " + row + " " + col);
					}
				}
				bIdx++;

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
			System.out.println("Connection established!");
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
		Server s = new Server(Integer.valueOf(args[0]));
	}
}

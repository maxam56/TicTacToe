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
			clientWriter.println("---|---|---");
			while (true) {
				try {
					
					//Wait for first move from client
					if (fillBoard(clientReader.readLine())) {
						//successful line read
					} else {
						clientWriter.println("Invalid move submission");
						continue;
					}
					
					//Make own move
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
		
		private boolean fillBoard(String move) {
			if (move.length() != 11) return false;
			int row, col;
			int count = 0;
			char mark;
			for (int i = 0; i < move.length(); i++) {
				if (count > 1) {
					clientWriter.println("Too many moves!");
					return false;
				}
				row = i/3;
				col = i%3;
				if (move.charAt(i) == '+' || move.charAt(i) == 'o') {
					mark = game.getMakr(row, col);
					if (Character.isLetter(move.charAt(i))) {
						if (!(move.charAt(i) == game.getMakr(row, col))) {
							game.placeMark(row, col);
							count++;
						}
					}
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

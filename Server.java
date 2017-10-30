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
		public ServWorker(final Socket client, final Server server) {
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

	}
	private ServerSocket socket;
	TicTacToe game;
	
	public Server(int portNum) {
		game = new TicTacToe();
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
			System.out.println("Server waiting for client connection...");
			
		}
	}
}

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.ConnectionToClient;

public class Server {

	private static ServerSocket serverSocket = null;
	private static int state;
	private static List<ConnectionToClient> clients;

	/** Bind Server to default port. */
	public static void bind(int serverPort) throws IOException {
		serverSocket = new ServerSocket(serverPort);
		state = 1;
	}

	public static void main(String args[]) throws IOException {
		clients = new ArrayList<ConnectionToClient>();
		state = 1;
		SeleniumPoller poller = SeleniumPoller.getInstance();
		poller.start();
		bind(11111);
		while (state == 1) {
			try {
				Socket client = serverSocket.accept();
				ConnectionToClient connection = new ConnectionToClient(client);
				clients.add(connection);
				connection.start();
			} catch (Exception e) {
				System.out.println("Connection failed");
			}
		}
		poller.shutdown();
		shutdown();
	}

	/** Shutdown the server. */
	public static void shutdown() throws IOException {		
		if (serverSocket != null) {
			serverSocket.close();
			serverSocket = null;
			state = 0;
		}
	}

	public static void remove(ConnectionToClient client) {
		clients.remove(client);
	}

	/**
	 * sends updates to every client
	 * 
	 * @param message
	 */
	public static void pushUpdate(Object message) {
		for (ConnectionToClient client : clients) {
			client.send(message);
		}
	}

}

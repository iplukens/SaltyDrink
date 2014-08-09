package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.Server;

public class ConnectionToClient extends Thread {

	private Server server;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private MessageHandler handler;

	public ConnectionToClient(Socket client) throws IOException {
		in = new ObjectInputStream(client.getInputStream());
		out = new ObjectOutputStream(client.getOutputStream());
		handler = new MessageHandler();
	}

	public void run() {
		Object message;
		while ((message = in) != null) {

			Object response = handler.process(message);
			send(response);
		}
		server.remove(this);
	}

	/**
	 * sends a message to the client
	 * 
	 * @param message
	 */
	public void send(Object message) {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

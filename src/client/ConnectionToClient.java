package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.json.JSONObject;

import messages.MessageHandler;
import server.Server;

public class ConnectionToClient extends Thread {

	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private MessageHandler handler;

	public ConnectionToClient(Socket client) throws IOException {
		in = new ObjectInputStream(client.getInputStream());
		out = new ObjectOutputStream(client.getOutputStream());		
		handler = new MessageHandler();
		this.client = client;
	}

	public void run() {
		System.out.println("Client connected. " + client.getRemoteSocketAddress().toString());
		Object message;
		try {
			while ((message = MessageHandler.extractMessage(in, "terminator")) != null) {
				System.out.println(message);
				JSONObject response = handler.process(message);
				send(response.toString());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("Removing client: " + client.getRemoteSocketAddress().toString());
		Server.remove(this);
		try {
			in.close();
			out.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

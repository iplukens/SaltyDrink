package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.MessageHandler;

import org.json.simple.JSONObject;

import server.Server;

public class ConnectionToClient extends Thread {

	private Socket client;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private MessageHandler handler;
	private String token;

	public ConnectionToClient(String clientId, Socket client)
			throws IOException {
		in = new ObjectInputStream(client.getInputStream());
		out = new ObjectOutputStream(client.getOutputStream());
		handler = new MessageHandler();
		this.client = client;
		token = clientId;
	}

	public void run() {
		System.out.println("Client connected. "
				+ client.getRemoteSocketAddress().toString());
		send(tokenResponse().toString());
		Object message;
		try {
			while ((message = MessageHandler.extractMessage(in)) != null) {
				System.out.println(message);
				JSONObject response = handler.process(token, message);
				send(response.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Removing client: "
				+ client.getRemoteSocketAddress().toString());
		Server.remove(this);
		try {
			in.close();
			out.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private Object tokenResponse() {
		System.out.println("Sending client their token...");
		JSONObject response = new JSONObject();
		response.put("type", "TOKEN");
		response.put("token", token);
		return response;
	}

	/**
	 * sends a message to the client
	 * 
	 * @param message
	 */
	public void send(Object message) {
		System.out.println("Sending: " + message + " to client " + token);
		try {
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getToken() {
		return token;
	}

}

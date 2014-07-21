package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.ClientThread;

public class Server {

	private static ServerSocket serverSocket = null;
	private static int state;
	
	/** Bind Server to default port. */
	public void bind(int serverPort) throws IOException {
		serverSocket = new ServerSocket(serverPort);
		state = 1; 
	}
	
	public static void main(String args[]) throws IOException{
		state = 1;
		SeleniumPoller poller = SeleniumPoller.getInstance();
		poller.start();
		while (state == 1) {
			try{
				Socket client = serverSocket.accept();
				new ClientThread(client);
			} catch (Exception e){
				System.out.println("Connection failed");
			}
		} 
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
	
}

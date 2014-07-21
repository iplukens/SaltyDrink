package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import server.SeleniumPoller;

public class ClientThread extends Thread implements Runnable {

	private PrintWriter toClient;
	Socket client;
	private boolean open;

	public ClientThread(Socket client) throws IOException {
		this.client = client;
		toClient = new PrintWriter(client.getOutputStream(), true);
		open = true;
	}

	public void run() {
		SeleniumPoller poller = SeleniumPoller.getInstance();
		while (open) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("I woke up!");
			toClient.println(poller.getResult().toString());
		}
	}

}

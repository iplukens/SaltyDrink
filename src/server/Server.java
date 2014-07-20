package server;

public class Server {

	public static void main(String args[]){
		SeleniumPoller poller = SeleniumPoller.getInstance();
		poller.run();
	}
	
}

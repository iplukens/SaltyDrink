package server;

import gamestate.GameState;
import gamestate.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

import client.ConnectionToClient;

public class Server {

	// private static Logger logger = Logger.getLogger(Server.class);

	private static ServerSocket serverSocket = null;
	private static int state;
	private static Map<String, ConnectionToClient> clients;
	private static Map<Long, GameState> games;
	private static long lastId;
	private static final String adminToken = "SUPERsecretADMINtokenTHATisENCRYPTEDprobably";

	/** Bind Server to default port. */
	public static void bind(int serverPort) throws IOException {
		serverSocket = new ServerSocket(serverPort);
		state = 1;
	}

	public static void main(String args[]) throws IOException {
		// System.setProperty("selenium.LOGGER", "selenium.txt");
		// configure logger

		clients = new HashMap<String, ConnectionToClient>();
		lastId = 0;
		games = new HashMap<>();
		int portNum = 11112;
		long id = addNewGame();
		addPlayerToGame(id, "LaserOrange", "token1");
		addPlayerToGame(id, "Jem and the Holograms", "token2");
		addPlayerToGame(id, "The Penis", "token3");
		addPlayerToGame(id, "Chrisgopher McAngles", "token4");
		state = 1;
		SeleniumPoller poller = SeleniumPoller.getInstance();
		poller.start();
		bind(portNum);
		System.out.println("Accepting on port " + portNum);
		while (state == 1) {
			try {
				Socket client = serverSocket.accept();
				String clientId = generateId();
				ConnectionToClient connection = new ConnectionToClient(
						clientId, client);
				clients.put(clientId, connection);
				connection.start();
			} catch (Exception e) {
				System.out.println("Connection failed");
			}
		}
		poller.shutdown();
		shutdown();
	}

	private static String generateId() {
		String id = UUID.randomUUID().toString();
		while (clients.keySet().contains(id)) {
			id = UUID.randomUUID().toString();
		}
		System.out.println("Generated id: " + id);
		return id;
	}

	/** Shutdown the server. */
	public static void shutdown() throws IOException {
		if (serverSocket != null) {
			serverSocket.close();
			serverSocket = null;
			state = 0;
		}
	}

	public synchronized static void remove(ConnectionToClient client) {
		clients.remove(client.getToken());
		removeFromGames(client.getToken());
	}

	private synchronized static void removeFromGames(String token) {
		for (long key : games.keySet()) {
			games.get(key).removePlayer(token);
		}
	}

	/**
	 * sends updates to every client
	 * 
	 * @param message
	 */
	public synchronized static void pushUpdate(JSONObject message) {
		for (String key : clients.keySet()) {
			clients.get(key).send(message.toJSONString());
		}
	}

	public synchronized static long addNewGame() {
		long id = lastId + 1;
		System.out.println("Created game " + id);
		games.put(id, new GameState());
		return id;
	}

	public synchronized static GameState getGame(long gameId) {
		return games.get(gameId);
	}

	public synchronized static boolean addPlayerToGame(long gameId,
			String playerName, String token) {
		try {
			games.get(gameId).addPlayer(playerName,
					new Player(token, playerName));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public synchronized static List<String> getPlayerSet(long id) {
		return games.get(id).getPlayerNames();
	}

	public static void sendToClient(String playerId, JSONObject message) {
		try {
			clients.get(playerId).send(message.toString());
		} catch (Exception e) {
			System.out.println("Could not send to client: " + playerId);
		}
	}

	public static void generateMatchups() {
		for (Long id : games.keySet()) {
			games.get(id).generateMatchups();
		}
	}

	public static void updateGame(Long gameId, String playerId, String token) {
		games.get(gameId).updatePlayer(token, playerId);
	}

	public static JSONObject getRoomState(long gameId) {
		return Server.getGame(gameId).getRoomStateResponse();
	}

	public static String getAdminToken() {
		return adminToken;
	}

}

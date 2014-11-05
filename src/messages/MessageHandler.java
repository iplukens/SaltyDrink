package messages;

import gamestate.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import server.Server;
import client.RequestType;

public class MessageHandler {

	public static Object extractMessage(ObjectInputStream in) {
		try {
			Object obj = in.readObject();
			if (obj == null) {
				return null;
			}
			return obj;
		} catch (IOException ioe) {
			System.out.println(ioe.getLocalizedMessage());
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public JSONObject process(String token, Object message) throws Exception {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse((String) message);
		RequestType type = null;
		String recievedToken = (String) jsonObject.get("token");
		if (!token.equals(recievedToken)) {
			throw new Exception("Client token did not match.  Expected: "
					+ token + " but got " + recievedToken);
		}
		type = RequestType.valueOf((String) jsonObject.get("type"));

		switch (type) {
		case ROOM:
			return processConnect(token, jsonObject);
		case BET:
			return processBid(token, jsonObject);
		case NAME_UPDATE:
			return processNameChange(token, jsonObject);
		case SHUTDOWN:
			return processShutdown(token, jsonObject);
		default:
			return null;
		}
	}

	private JSONObject processShutdown(String token, JSONObject jsonObject) {
		if (token.equals(Server.getAdminToken())) {
			try {
				Server.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private JSONObject processNameChange(String token, JSONObject jsonObject) {
		System.out.println("process name change...");
		System.out.println(jsonObject);
		Long gameId = null;
		gameId = (long) jsonObject.get("gameId");
		Server.updateGame(gameId, (String) jsonObject.get("playerId"), token);
		return Server.getRoomState(gameId);
	}

	@SuppressWarnings("unchecked")
	private JSONObject processConnect(String token, JSONObject jsonObject)
			throws JSONException, Exception {
		System.out.println("processing connect request...");
		System.out.println(jsonObject);
		JSONObject response = new JSONObject();
		boolean addResult = false;
		Long gameId = null;
		gameId = (long) jsonObject.get("gameId");
		addResult = Server.addPlayerToGame(gameId,
				(String) jsonObject.get("playerId"), token);
		if (!addResult) {
			response.put("type", "ROOM_STATE");
			response.put("error", "join room failed!");
		} else {
			response = Server.getRoomState(gameId);
		}
		System.out.println("Connect Response: " + response);
		return response;
	}

	@SuppressWarnings("unchecked")
	private JSONObject processBid(String token, JSONObject jsonObject) {
		System.out.println("processing bid...");
		System.out.println(jsonObject);
		try {
			String betColor = (String) jsonObject.get("betColor");
			Integer betAmount = (Integer) jsonObject.get("betAmount");
			Integer gameId = (Integer) jsonObject.get("gameId");
			GameState game = Server.getGame(gameId);
			game.addBid(token, betColor, betAmount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject response = new JSONObject();
		response.put("type", "BID_RESPONSE");
		return response;
	}
}

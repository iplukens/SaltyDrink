package messages;

import gamestate.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import server.SeleniumPoller;
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
		JSONObject jsonObject = null;		
		try {
			jsonObject = new JSONObject((String) message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestType type = null;
		String recievedToken = (String) jsonObject.get("token");
		if (!token.equals(recievedToken)) {
			throw new Exception("Client token did not match.  Expected: "
					+ token + " but got " + recievedToken);
		}
		try {
			type = RequestType.valueOf((String) jsonObject.get("type"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch (type) {
		case ROOM:
			return processConnect(token, jsonObject);
		case BET:
			return processBid(token, jsonObject);
		default:
			return null;
		}
	}

	private JSONObject processConnect(String token, JSONObject jsonObject)
			throws JSONException, Exception {
		System.out.println("processing connect request...");
		System.out.println(jsonObject);
		JSONObject response = new JSONObject();
		boolean addResult = false;
		Integer gameId = null;
		try {
			gameId = (Integer) jsonObject.get("gameId");
			addResult = Server.addPlayerToGame(gameId,
					(String) jsonObject.getString("playerId"), token);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (!addResult) {
			try {
				response.put("type", "ROOM_STATE");
				response.put("error", "join room failed!");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				response.put("type", "ROOM_STATE");
				response.put("playerSet", Server.getPlayerSet(gameId));
				response.put("betStatus", SeleniumPoller.getInstance()
						.getBetStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Connect Response: " + response);
		return response;
	}

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
		try {
			response.put("type", "BID_RESPONSE");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
}

package gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONObject;

import server.Server;

public class GameState {

	private static final String SALTY_BOT_TOKEN = "SALTY-BOT-TOKEN";

	private Map<String, Player> players;
	private long roundsPlayed;

	public GameState() {
		players = new HashMap<>();
		roundsPlayed = 0;
	}

	public Set<String> getPlayerNames() {
		Set<String> result = new HashSet<>();
		for (String key : players.keySet()) {
			result.add(players.get(key).getPlayerName());
		}
		return result;
	}

	public void addPlayer(String id, Player player) {
		players.put(player.getToken(), player);
	}

	public void removePlayer(String playerId) {
		players.remove(playerId);
	}

	public void addBid(String token, String betColor, int betAmount) {
		Player player = players.get(token);
		player.setCurrentBet(new Bet(betColor, betAmount));
		if (player.getCurrentOpponentToken() == SALTY_BOT_TOKEN) {
			// TODO: SALT DROID
		} else {
			Player opponent = players.get(player.getCurrentOpponentToken());
			if (opponent.hasBid()) {
				sendOpponentBidResponse(token, opponent);
			}
		}
	}

	public boolean everyPlayerHasBid() {
		for (String key : players.keySet()) {
			if (players.get(key).getCurrentBet() == null) {
				return false;
			}
		}
		return true;
	}

	public void generateMatchups() {
		roundsPlayed++;
		clearPlayerCurrentBids();
		List<String> playerList = new ArrayList<>(players.keySet());
		if (playerList.size() % 2 != 0) {
			playerList.add(SALTY_BOT_TOKEN);
		}
		while (playerList.size() > 0) {
			int firstPlayer = new Random().nextInt(playerList.size());
			String firstPlayerToken = playerList.remove(firstPlayer);
			int secondPlayer = new Random().nextInt(playerList.size());
			String secondPlayerToken = playerList.remove(secondPlayer);
			setMatchUp(firstPlayerToken, secondPlayerToken);
		}
	}

	private void clearPlayerCurrentBids() {
		for (String key : players.keySet()) {
			players.get(key).clearCurrentBet();
		}
	}

	private void setMatchUp(String firstPlayerToken, String secondPlayerToken) {
		System.out.println("Generating matchup of: " + firstPlayerToken
				+ " versus " + secondPlayerToken);
		players.get(firstPlayerToken)
				.setCurrentOpponentToken(secondPlayerToken);
		Server.sendToClient(firstPlayerToken,
				matchupResponse(secondPlayerToken));
		players.get(secondPlayerToken)
				.setCurrentOpponentToken(firstPlayerToken);
		Server.sendToClient(secondPlayerToken,
				matchupResponse(firstPlayerToken));
	}

	@SuppressWarnings("unchecked")
	private JSONObject matchupResponse(String secondPlayerToken) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "MATCH_UP");
		if (secondPlayerToken.equals(SALTY_BOT_TOKEN)) {
			jsonObject.put("opponentName", SALTY_BOT_TOKEN);
		} else {
			jsonObject.put("opponentName", players.get(secondPlayerToken)
					.getPlayerName());
		}
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private void sendOpponentBidResponse(String playerToken, Player opponent) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "OPPONENT_BID");
		jsonObject.put("color", opponent.getCurrentBet().getColor());
		jsonObject.put("bidAmount", opponent.getCurrentBet().getAmount());
		Server.sendToClient(playerToken, jsonObject);
	}

}

package gamestate;

public class Player {
	private String token;
	private String playerName;
	private Bet currentBet;
	private long drinksHad;
	private String currentOpponentToken;

	public Player(String token, String name) {
		setToken(token);
		setPlayerName(name);
	}

	public long getDrinksHad() {
		return drinksHad;
	}

	public void setDrinksHad(long drinksHad) {
		this.drinksHad = drinksHad;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Bet getCurrentBet() {
		return currentBet;
	}

	public void setCurrentBet(Bet currentBet) {
		this.currentBet = currentBet;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCurrentOpponentToken() {
		return currentOpponentToken;
	}

	public void setCurrentOpponentToken(String currentOpponentToken) {
		this.currentOpponentToken = currentOpponentToken;
	}

	public void clearCurrentBet() {
		this.currentBet = null;
	}

	public boolean hasBid() {
		return currentBet != null;
	}

}

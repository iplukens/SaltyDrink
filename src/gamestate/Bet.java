package gamestate;

public class Bet {
	private String color;
	private int amount;
	
	public Bet(String color, int betAmount){
		this.setColor(color);
		this.setAmount(betAmount);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}	
}

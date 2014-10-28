package server;

import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumPoller extends Thread implements Runnable {
	
	private BetStatus betStatus = BetStatus.CLOSED;
	private static SeleniumPoller instance;
	private boolean running = true;
	private WebDriver driver = new FirefoxDriver();

	private SeleniumPoller() {
	}

	public static SeleniumPoller getInstance() {
		if (instance == null) {
			instance = new SeleniumPoller();
		}
		return instance;
	}

	public void run() {
		driver.get("http://www.saltybet.com");
		WebElement status = driver
				.findElement(By.xpath("//*[@id='betstatus']"));
		while (running) {
			updateBetStatus(status.getText());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateBetStatus(String betStatusText) {
		// TODO: notify some other thread when result changes
		if (betStatusText.equals("Bets are OPEN!")) {
			if (!betStatus.equals(BetStatus.OPEN)) {
				Server.generateMatchups();
				setResult(BetStatus.OPEN);
			}
		} else if (betStatusText.equals("Bets are locked until the next match.")) {
			if (!betStatus.equals(BetStatus.CLOSED)) {
				setResult(BetStatus.CLOSED);
			}
		} else if (betStatusText.contains(" wins! Payouts to Team Blue.")) {
			if (!betStatus.equals(BetStatus.BLUE_WINS)) {
				setResult(BetStatus.BLUE_WINS);
			}
		} else if (betStatusText.contains(" wins! Payouts to Team Red.")) {
			if (!betStatus.equals(BetStatus.RED_WINS)) {
				setResult(BetStatus.RED_WINS);
			}
		} else if (!betStatus.equals(BetStatus.TIE)) {
			setResult(BetStatus.TIE);
		}
	}

	public void shutdown() {
		running = false;
		driver.quit();
	}

	public BetStatus getBetStatus() {
		return betStatus;
	}

	public void setResult(BetStatus betStatus) {
		System.out.println(betStatus);
		this.betStatus = betStatus;
		Server.pushUpdate(createRequest());
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject createRequest() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", "RESULT");
		jsonObject.put("betStatus", betStatus.toString());
		return jsonObject;
	}

}

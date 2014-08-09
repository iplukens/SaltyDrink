package server;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumPoller extends Thread implements Runnable {

	private Result result = Result.CLOSED;
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
			updateResult(status.getText());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateResult(String resultText) {
		// TODO: notify some other thread when result changes
		if (resultText.equals("Bets are OPEN!")) {
			if (!result.equals(Result.OPEN)) {
				setResult(Result.OPEN);
			}
		} else if (resultText.equals("Bets are locked until the next match.")) {
			if (!result.equals(Result.CLOSED)) {
				setResult(Result.CLOSED);
			}
		} else if (resultText.contains(" wins! Payouts to Team Blue.")) {
			if (!result.equals(Result.BLUE_WINS)) {
				setResult(Result.BLUE_WINS);
			}
		} else if (resultText.contains(" wins! Payouts to Team Red.")) {
			if (!result.equals(Result.RED_WINS)) {
				setResult(Result.RED_WINS);
			}
		} else if (!result.equals(Result.TIE)) {
			setResult(Result.TIE);
		}
	}

	public void shutdown() {
		running = false;
		driver.quit();
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		System.out.println(result);
		this.result = result;
		Server.pushUpdate(result.toString());
	}

}

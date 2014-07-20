package server;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumPoller implements Runnable{
	
	private Result result = Result.CLOSED;
	private static SeleniumPoller instance;
	
	private SeleniumPoller(){
		instance.run();
	}
	
	public static SeleniumPoller getInstance(){
		if (instance == null){
			instance = new SeleniumPoller();
		}
		return instance;
	}
	
	public void run() {
		WebDriver driver = new FirefoxDriver();
		driver.get("http://www.saltybet.com");
		WebElement status = driver.findElement(By.xpath("//*[@id='betstatus']"));
		while(true){
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
		if(resultText.equals("Bets are OPEN!") && !result.equals(Result.OPEN)){
			setResult(Result.OPEN);
		}
		else if(resultText.equals("Bets are locked until the next match.") && !result.equals(Result.CLOSED)){
			setResult(Result.CLOSED);
		}
		else if(resultText.contains(" wins! Payouts to Team Blue.") && !result.equals(Result.BLUE_WINS)){
			setResult(Result.BLUE_WINS);
		}
		else if(resultText.contains(" wins! Payouts to Team Red.") && !result.equals(Result.RED_WINS)){
			setResult(Result.RED_WINS);
		}
		else if (!result.equals(Result.TIE)){
			setResult(Result.TIE);
		}
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

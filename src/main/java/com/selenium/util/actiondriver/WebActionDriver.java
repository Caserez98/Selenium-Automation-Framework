package com.selenium.util.actiondriver;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.selenium.setup.SelTestCase;
import com.selenium.util.CommonUtil;
import com.selenium.util.enums.BaseAppUrl;
import com.selenium.util.enums.Browser;

/**
 * This class is responsible for GUI interaction. It contains functions like
 * type, click , select etc
 *
 */
public class WebActionDriver extends BaseActionDriver {

	CommonUtil common;

	public void initializeLogging() {
		common = SelTestCase.getCommon();
	}

	public Set<String> getWindowHandles() {
		return webDriver.getWindowHandles();
	}

	public void get(BaseAppUrl appUrl) throws Exception {
		webDriver.get(new CommonUtil().getSiteUrl(appUrl));
		maximizeBrowser();
	}

	public void get(String Url) throws Exception {
		webDriver.get(Url);
		maximizeBrowser();
	}

	public void getWithoutMaximizeBrowser(String Url) throws Exception {
		webDriver.get(Url);
	}

	public void quit() {
		webDriver.quit();
	}

	public void maximizeBrowser() {
		if (!(getBrowserName().toLowerCase().contains(Browser.IOS.toString().toLowerCase())
				|| getBrowserName().toLowerCase().contains(Browser.ANDROID.toString().toLowerCase()))) {
			// Toolkit toolkit = Toolkit.getDefaultToolkit();
			// int screenWidth = (int) toolkit.getScreenSize().getWidth();
			// int screenHeight = (int) toolkit.getScreenSize().getHeight();
			// Dimension screenResolution = new Dimension(screenWidth, screenHeight);
			// driver.manage().window().setSize(screenResolution);
			try {
				webDriver.manage().window().maximize();
			} catch (Exception e) {
				common.log(e.getMessage());
			}
			if (getBrowserName().equalsIgnoreCase(Browser.HEAD_LESS.toString())) {
				webDriver.manage().window().setSize(new Dimension(1920, 1080));
			}
		}
	}

	/**
	 * This function will fetch the element from the application.
	 *
	 * @param locator
	 * @return
	 * @throws Exception
	 */
	public WebElement getElement(By locator) throws Exception {
		try {
			return waitForElementToBePresent(locator);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public WebElement getClickableElement(By locator) throws Exception {
		try {
			return waitForElementToBeClickable(locator);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	/**
	 * It waits for element to be visible on page
	 *
	 * @param locator
	 * @return
	 * @throws Exception
	 * @throws TimeoutException
	 */
	public <T> WebElement waitForElementToBeVisible(final T locator) throws Exception {
		// common.log("Wait for element to be visible : " + locator);
		waitForAngularRequestsToFinish();
		try {
//			 mouseHover(locator);
			WebDriverWait wait = new WebDriverWait(webDriver, SelTestCase.WAIT_TIMEOUT);
			WebElement element = null;
//			element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			if (locator.getClass().getName().contains("By")) {
				element = wait.until(ExpectedConditions.visibilityOfElementLocated((By) locator));
				// common.log(String.format("Element : %s Found !!!", locator));
			} else if (locator.getClass().getName().contains("String")) {
				element = wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + (String) locator + "')]")));
				// common.log(String.format("Text : %s Found !!!", (String) locator));
			} else if (locator.getClass().getName().contains("WebElement")) {
				element = wait.until(ExpectedConditions.visibilityOf((WebElement) locator));
				// common.log(String.format("Element : %s Found !!!", locator));
			}
			return element;
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			if (locator.getClass().getName().contains("WebElement")) {
				throw new StaleElementReferenceException(s.getAdditionalInformation());
			}
			Thread.sleep(2000);
			waitForElementToBeVisible(locator);
		}
		return null;
	}

	public <T> WebElement waitForElementToBePresent(final T locator, int timeOut) throws Exception {
		// common.log("Wait for element to be present in DOM : " + locator);
		waitForAngularRequestsToFinish();
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, timeOut);
			WebElement element = null;
			if (locator.getClass().getName().contains("By")) {
				element = wait.until(ExpectedConditions.presenceOfElementLocated((By) locator));
				// common.log(String.format("Element : %s Found !!!", locator));
			} else if (locator.getClass().getName().contains("String")) {
				element = wait.until(ExpectedConditions
						.presenceOfElementLocated(By.xpath("//*[contains(text(),'" + (String) locator + "')]")));
				// common.log(String.format("Text : %s Found !!!", (String) locator));
			}
			return element;
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			waitForElementToBePresent(locator, timeOut);
		}
		return null;
	}

//	public WebElement waitForElementToBePresent(final String text) throws Exception, TimeoutException {
//		By locator = By.xpath("//*[contains(text(),'" + text + "')]");
//		//common.log("Wait for element to be present in DOM : " + locator);
//		WebElement element = waitForElementToBePresent(locator);
//		return element;
//	}

	public <T> WebElement waitForElementToBePresent(final T locator) throws Exception {
		waitForAngularRequestsToFinish();
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, SelTestCase.WAIT_TIMEOUT);
			WebElement element = null;
			if (locator.getClass().getName().contains("By")) {
				element = wait.until(ExpectedConditions.presenceOfElementLocated((By) locator));
				// common.log(String.format("Element : %s Found !!!", locator));
			} else if (locator.getClass().getName().contains("String")) {
				element = wait.until(ExpectedConditions
						.presenceOfElementLocated(By.xpath("//*[contains(text(),'" + (String) locator + "')]")));
				// common.log(String.format("Text : %s Found !!!", (String) locator));
			}
			return element;
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			waitForElementToBePresent(locator);
		}
		return null;
	}

	public WebElement waitForElementToBeClickable(By locator) throws Exception, TimeoutException {
		waitForAngularRequestsToFinish();
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, SelTestCase.WAIT_TIMEOUT);
			WebElement element = null;
			element = wait.until(ExpectedConditions.elementToBeClickable(locator));
			return element;
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			waitForElementToBeClickable(locator);
		}
		return null;
	}

	public WebElement waitForElementToBeClickable(By locator, int timeOut) throws Exception {
		waitForAngularRequestsToFinish();
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, timeOut);
			WebElement element = null;
			element = wait.until(ExpectedConditions.elementToBeClickable(locator));
			return element;
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			waitForElementToBeClickable(locator);
		}
		return null;
	}

	public <T> WebElement waitForElementToBeVisible(final T locator, int timeOut) throws Exception {
		// common.log("Wait for element to be visible on web page: " + locator);
		waitForAngularRequestsToFinish();
		try {
			setImplicitWaitOnDriver(0);
			WebDriverWait wait = new WebDriverWait(webDriver, timeOut);
			WebElement element = null;
			if (locator.getClass().getName().contains("By")) {
				element = wait.until(ExpectedConditions.visibilityOfElementLocated((By) locator));
				// common.log(String.format("Element : %s Found !!!", locator));
			} else if (locator.getClass().getName().contains("String")) {
				element = wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + (String) locator + "')]")));
				// common.log(String.format("Text : %s Found !!!", (String) locator));
			} else if (locator.getClass().getName().contains("WebElement")) {
				element = wait.until(ExpectedConditions.visibilityOf((WebElement) locator));
				// common.log(String.format("Element : %s Found !!!", locator));
			}
			return element;
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			if (locator.getClass().getName().contains("WebElement")) {
				throw new StaleElementReferenceException(s.getAdditionalInformation());
			}
			Thread.sleep(2000);
			waitForElementToBeVisible(locator, timeOut);
		} finally {
			setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
		}
		return null;
	}

//	public WebElement waitForElementToBeVisible(String text, int timeOut) throws TimeoutException, Exception {
//		//common.log("Wait for text to be visible on web page: " + text);
//		WebElement element = waitForElementToBeVisible(By.xpath("//*[contains(text(),'" + text + "')]"), timeOut);
//		//common.log(String.format("Text : %s Found !!!", text));
//		return element;
//	}

	public boolean isElementVisible(By locator) throws Exception {
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, SelTestCase.WAIT_TIMEOUT);
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isElementVisible(By locator, int waitTime) throws Exception {
		// common.log("Is following element Visible : " + locator);
		waitForAngularRequestsToFinish();
		try {
			setImplicitWaitOnDriver(0);
			WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
		}

	}

	public boolean isElementVisible(String text, int waitTime) throws Exception {
		By locator = By.xpath("//*[contains(text(),'" + text + "')]");
		return isElementVisible(locator, waitTime);
	}

	public boolean isElementPresent(By locator, int waitTime) throws Exception {
		// common.log("Is following element present : " + locator);
		waitForAngularRequestsToFinish();
		try {
			setImplicitWaitOnDriver(0);
			WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
			wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			return true;
		} catch (Throwable e) {
			return false;
		} finally {
			setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
		}
	}

	public <T> void waitForElementNotPresent(T locator, int waitTime) throws Exception, TimeoutException {
		waitForAngularRequestsToFinish();
		// common.log("Waiting for element not present : " + locator);
		try {
			setImplicitWaitOnDriver(0);
			WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
//			wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
			if (locator.getClass().getName().contains("By")) {
				wait.until(ExpectedConditions.invisibilityOfElementLocated((By) locator));
			} else if (locator.getClass().getName().contains("String")) {
				wait.until(ExpectedConditions
						.invisibilityOfElementLocated(By.xpath("//*[contains(text(),'" + (String) locator + "')]")));
			}
			// common.log("Element Disappeared !!!");
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
		}
	}

	public List<WebElement> getElements(By locator, int waitTime) throws Exception {
		waitForAngularRequestsToFinish();
		try {
			setImplicitWaitOnDriver(0);
			WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
			return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
		} catch (Exception e) {
			// return true;
			throw new Exception(e);
		} finally {
			setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
		}
	}

//	public void waitForElementNotPresent(String text, int waitTime) throws Exception, TimeoutException {
//		waitForAngularRequestsToFinish();
//		//common.log("Waiting for element not present : " + text);
//		try {
//			setImplicitWaitOnDriver(0);
//			WebDriverWait wait = new WebDriverWait(webDriver, waitTime);
//			wait.until(
//					ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(text(),'" + text + "')]")));
//		} catch (Exception e) {
//			throw new Exception(e);
//		} finally {
//			setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
//		}
//	}

	/**
	 * It select the option from a drop down by using text
	 *
	 * @param locator
	 * @param testData
	 * @throws Exception
	 */
	public void selectByText(By locator, String testData) throws Exception {
		WebElement element;
		try {
			// mouseHover(locator);
			click(locator);
			element = waitForElementToBePresent(locator);
			selectByText(element, testData);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public void selectByText(WebElement element, String testData) throws Exception {
		boolean isSelected = false;
		Select select;
		try {
			select = new Select(element);
			List<WebElement> selectedOptions = select.getAllSelectedOptions();
			for (WebElement selectedOption : selectedOptions) {
				if (selectedOption.getText().trim().toLowerCase().contains(testData.toLowerCase())) {
					isSelected = true;
					// common.log("Option already selected");
					break;
				}
			}
			if (!isSelected) {
				select.selectByVisibleText(testData);
			}
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public void deSelectAll(By locator) throws Exception {
		WebElement element;
		try {
			element = getElement(locator);
			Select s2 = new Select(element);
			s2.deselectAll();
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public String getFirstSelectedOption(By locator) throws Exception {
		Select select = new Select(webDriver.findElement(locator));
		WebElement option = select.getFirstSelectedOption();
		return option.getText();
	}

	public List<WebElement> getDropdownOptions(By locator) throws Exception {
		Select select = new Select(webDriver.findElement(locator));
		return select.getOptions();
	}

	public void selectByIndex(By locator, int index) throws Exception {
		WebElement element;
		try {
			element = getElement(locator);
			Select s2 = new Select(element);
			s2.selectByIndex(index);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public boolean isOptionExistInDropDown(By locator, String option) throws Exception {
		WebElement element = getElement(locator);
		Select select = new Select(element);
		for (WebElement list : select.getOptions()) {
			// common.log(list.getText());
			if (list.getText().equalsIgnoreCase(option)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * It select the option from a drop down by value
	 *
	 * @param locator
	 * @param testData
	 * @throws Exception
	 */
	public void selectByValue(By locator, String testData) throws Exception {
		WebElement element;
		try {
			element = waitForElementToBePresent(locator);
			Select s2 = new Select(element);
			s2.selectByValue(testData);
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public void clearTextBoxValue(By locator) throws Exception {
		// common.log("Cleaning textbox value");
		boolean isTextBoxCleared = false;
		for (int i = 0; i < 10; i++) {
			if (getElement(locator).getAttribute("value").trim().length() == 0) {
				isTextBoxCleared = true;
				break;
			}
			try {
				if (!getCapabilities().getPlatform().toString().equalsIgnoreCase("MAC")) {
					try {
						Actions action = getActionsInstance();
						action.click(getElement(locator)).sendKeys(Keys.chord(Keys.CONTROL, "a"))
								.sendKeys(Keys.BACK_SPACE).build().perform();
					} catch (Exception e) {
						System.out.println("Getting following error : " + e.getMessage());
					}
				}
				if (!(getInputBoxValue(locator).length() == 0)) {
					try {
						click(locator);
					} catch (Exception e) {
						e.printStackTrace();
						// common.log("Ignoring error : " + e.getMessage());
					}
					try {
						getElement(locator).clear();
					} catch (Exception e) {
						e.printStackTrace();
						// common.log("Ignoring error : " + e.getMessage());
					}
				}
				if (getElement(locator).getAttribute("value").trim().length() != 0) {
					try {
						executeJavaScript("arguments[0].scrollIntoView(true);", getElement(locator));
						getElement(locator).sendKeys(Keys.CONTROL + "a");
						getElement(locator).sendKeys(Keys.DELETE);
					} catch (Exception e) {
						e.printStackTrace();
						// common.log("Ignoring error : " + e.getMessage());
					}
				}
				System.out.println("Input Field value after executing clear command : "
						+ getElement(locator).getAttribute("value").trim());
				if (getElement(locator).getAttribute("value").trim().length() == 0) {
					// common.log("Cleared textbox value");
					isTextBoxCleared = true;
					break;
				}
				// common.log("Text value not cleared. Trying again");
				Thread.sleep(2000);
			} catch (Throwable t) {
				t.printStackTrace();
				throw new Exception(t);
			}
		}
		if (!isTextBoxCleared) {
			throw new Exception("Not able to clear text box value");
		}
	}

	/**
	 * It closes the browser
	 *
	 */
	public void closeBrowser() {
		try {
			if (webDriver != null) {
				// common.log("Terminating Browser ");
				webDriver.quit();
				webDriver = null;
			} else {
				// common.log("Browser is already closed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// common.log("Error handled which came while closing browser ");
			webDriver = null;
		}

	}

	/**
	 * It will type the test data into an input box
	 *
	 * @param locator
	 * @param testData
	 * @throws Exception
	 */
	public void type(By locator, CharSequence... testData) throws Exception {
		String logText = "";
		for (int i = 0; i < testData.length; i++) {
			logText = logText + testData[i];
		}
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, SelTestCase.WAIT_TIMEOUT);
			WebElement element = null;
			element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			// try {
			// mouseHover(element);
			// } catch (Exception e) {
			// e.printStackTrace();
			// common.log("Ignoring following error : " + e.getMessage());
			// }
			if (getBrowserName().toLowerCase().contains(Browser.IE.toString().toLowerCase())) {
				try {
					click(locator);
				} catch (Exception e) {
					// common.log("Ignoring following error : " + e.getMessage());
				}
				try {
					element.clear();
				} catch (Exception e) {
					// common.log("Ignoring following error : " + e.getMessage());
				}
				element.sendKeys(testData);
			} else if (getBrowserName().toLowerCase().contains(Browser.SAFARI.toString().toLowerCase())) {
				click(locator);
				element.clear();
				element.sendKeys(testData);
			} else {
				try {
					if (element.isDisplayed()) {
						element.clear();
						element.click();
						element.sendKeys(testData);
					}
				} catch (Exception e) {
					// common.log("Getting following error : " + e.getMessage());
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception(t);
		}
	}

	public void typeUsingJavaScript(By locator, String testData) throws Exception {
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, SelTestCase.WAIT_TIMEOUT);
			WebElement element = null;
			element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript("arguments[0].setAttribute('value', '" + testData + "');", element);

		} catch (Throwable t) {
			throw new Exception(locator + " is missing " + t);
		}
	}

	public void typeUsingActionApi(By locator, String testData) throws Exception {
		for (int i = 0; i <= 10; i++) {
			try {
				testData = testData.toString().trim();
				Actions actions = new Actions(webDriver);
				Thread.sleep(1000);
				try {
					waitForElementToBePresent(locator).clear();
				} catch (Exception e) {
					// common.log("Getting following error : " + e.getMessage());
				}
				actions.click(waitForElementToBePresent(locator));
				waitForElementToBePresent(locator).clear();
				actions.sendKeys(testData).build().perform();
				break;
			} catch (Throwable e) {
				common.log(e.getMessage());
			}
		}
	}

	public void blurUsingJavaScript(final By locator) throws Exception {
		try {
			// common.log("Blurring element");
			((JavascriptExecutor) webDriver).executeScript("arguments[0].blur()", waitForElementToBePresent(locator));
		} catch (Throwable t) {
			throw new Exception(locator + " is missing " + t);
		}
	}

	public void sendKeys(By locator, Object testData) throws Exception {
		testData = testData.toString().trim();
		// common.log("Typing value : " + testData);
		webDriver.findElement(locator).clear();
		webDriver.findElement(locator).click();
		webDriver.findElement(locator).sendKeys(testData.toString());
	}

	/**
	 * It clicks on web element
	 *
	 * @param locator
	 * @throws Exception
	 */
	public void click(By locator) throws Exception {
		try {
			// mouseHover(locator);
			waitForElementToBePresent(locator);
			waitForElementToBeClickable(locator).click();
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			clickUsingJavaScript(locator);
		} catch (Exception e) {
			common.log("Error message received " + e.getMessage());
			waitForElementToBePresent(locator, 5);
			clickUsingJavaScript(locator);
		} catch (Throwable t) {
			if (t.getMessage().toLowerCase().contains("JavaScript error in async script".toLowerCase())) {
				common.log("ignore java script error");
			} else {
				throw new Exception(t);
			}
		}
	}

	public void click(String text) throws Exception {
		By locator = By.xpath("//*[contains(text(),'" + text + "')]");
		mouseHover(locator);
		try {
			try {
				waitForElementToBeClickable(locator).click();
			} catch (Exception e) {
				e.printStackTrace();
				common.log("Error message received " + e.getMessage());
				waitForElementToBePresent(locator, 5);
				clickUsingJavaScript(locator);
			}
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			clickUsingActionApi(locator);
		} catch (Throwable t) {
			throw new Exception(locator + " is missing " + t);
		}
	}

	public void clickAndWaitForElementToDisappear(By locator, int timeOut) throws Exception {
		boolean isElementDisappeared = false;
		for (int i = 0; i < 2; i++) {
			try {
				if (i == 0) {
					click(locator);
				} else {
					if (isElementVisible(locator, 1)) {
						clickUsingJavaScript(locator);
					}
				}
				waitForElementNotPresent(locator, timeOut);
				isElementDisappeared = true;
				break;
			} catch (Throwable t) {
				t.printStackTrace();
				common.log("Trying to click again !!");
			}
		}
		if (!isElementDisappeared) {
			throw new Exception(
					"Issue : Element " + locator + " still displayed. It must get disappear after click !!!");
		}
	}

	public void clickUsingJSAndWaitForElementToDisappear(By locator, int timeOut) throws Exception {
		boolean isElementDisappeared = false;
		Throwable error = null;
		for (int i = 0; i < 2; i++) {
			try {
				if (i == 1) {
					if (!isElementVisible(locator, 1)) {
						isElementDisappeared = true;
						break;
					}
				}
				waitForElementToBeVisible(locator);
				clickUsingJavaScript(locator);
				waitUntilElementDisappear(locator, timeOut);
				isElementDisappeared = true;
				break;
			} catch (Throwable t) {
				error = t;
				common.log("Trying to click again !!");
			}
		}
		if (!isElementDisappeared) {
			throw new Exception(error);
		}
	}

	public void clickAndWaitForNextElementToBeVisible(By clickLocator, By locatorToDisplay) throws Exception {
		boolean isElementFound = false;
		Throwable error = null;
		for (int i = 0; i < 3; i++) {
			try {
				if (i == 0) {
					click(clickLocator);
				}
				if (i == 1) {
					if (isElementVisible(clickLocator, 1)) {
						clickUsingJavaScript(clickLocator);
					}
				}
				waitForElementToBeVisible(locatorToDisplay);
				isElementFound = true;
				break;
			} catch (Throwable t) {
				common.log(t.getMessage());
				error = t;
				common.log("Trying to click again !!");
			}
		}
		if (!isElementFound) {
			throw new Exception(error);
		}
	}

	public void clickAndWaitForNextElementToBePresent(By clickLocator, By nextLocatorToBePresent) throws Exception {
		boolean isElementFound = false;
		Throwable error = null;
		for (int i = 0; i < 2; i++) {
			try {
				if (i == 0) {
					click(clickLocator);
				}
				if (i == 1) {
					if (isElementPresent(clickLocator, 1)) {
						clickUsingJavaScript(clickLocator);
					}
				}
				waitForElementToBePresent(nextLocatorToBePresent);
				isElementFound = true;
				break;
			} catch (Throwable t) {
				error = t;
				common.log("Trying to click again !!");
			}
		}
		if (!isElementFound) {
			throw new Exception(error);
		}
	}

	public void clickUntilNewPageOpened(By locator, By newPageLocator) throws Exception {
		waitForElementToBePresent(locator);
		for (int i = 0; i < 3; i++) {
			try {
				if (webDriver.getCapabilities().getBrowserName()
						.equalsIgnoreCase(Browser.INTERNET_EXPLORER.toString())) {
					if (isElementPresent(locator, 3)) {
						try {
							clickUsingJavaScript(locator);
						} catch (Exception e) {
							System.out.println(e.getMessage());
							clickUsingActionApi(locator);
						}
					}
				} else {
					if (isElementPresent(locator, 3)) {
						click(locator);
					}
				}
				if (isElementPresent(newPageLocator, 5)) {
					break;
				}
			} catch (Exception e) {
				// common.log("Error Occurred : " + e.getMessage());
			}
		}

	}

	public void waitUntilElementDisappear(By locator, int timeOut) throws TimeoutException, Exception {
		boolean isElementDisappeared = false;
		for (int i = 0; i < timeOut; i++) {
			if (!isElementVisible(locator, 1)) {
				isElementDisappeared = true;
				break;
			}
			// common.log("waiting for element to get disappear : " + locator);
			Thread.sleep(1000);
		}
		if (!isElementDisappeared) {
			throw new Exception("Issue : Element " + locator + " still displayed !!!");
		}
	}

	/**
	 * It clicks on web element
	 *
	 * @param locator
	 * @throws Exception
	 */
	public void submitForm(By locator) throws Exception {
		try {
			getElement(locator).submit();
		} catch (Throwable t) {
			throw new Exception(locator + " is missing " + t);
		}
	}

	public void mouseHover(By locator) throws Exception {
		// WebElement target = waitForElementToBePresent(locator);
		// ((JavascriptExecutor)
		// driver).executeScript("arguments[0].scrollIntoView(true);", target);
		try {
			if (!(getBrowserName().toLowerCase().contains(Browser.IE.toString().toLowerCase())
					|| getBrowserName().toLowerCase().contains(Browser.IOS.toString().toLowerCase())
					|| getBrowserName().toLowerCase().contains(Browser.ANDROID.toString().toLowerCase())
					|| getBrowserName().toLowerCase().contains(Browser.SAFARI.toString().toLowerCase()))) {
				// Coordinates cor = ((Locatable)
				// waitForElementToBePresent(locator)).getCoordinates();
				// cor.inViewPort();
				((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);",
						waitForElementToBePresent(locator));
			}
		} catch (TimeoutException t) {
			throw new Exception(t);
		} catch (Exception e) {
			e.printStackTrace();
			// common.log("Ignoring issue occurred due to mouse hover !!");
		}
	}

	public void mouseHover(WebElement element) throws Exception {
		if (!(getBrowserName().toLowerCase().contains(Browser.IE.toString().toLowerCase())
				|| getBrowserName().toLowerCase().contains(Browser.IOS.toString().toLowerCase())
				|| getBrowserName().toLowerCase().contains(Browser.ANDROID.toString().toLowerCase())
				|| getBrowserName().toLowerCase().contains(Browser.SAFARI.toString().toLowerCase()))) {
			// Coordinates cor = ((Locatable) element).getCoordinates();
			// cor.inViewPort();
			((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
		}
	}

	public void scrollUp() {
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("scroll(0, -1000);");
	}

	public void scrollDown() {

		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	public void doubleClick(By locator) throws Exception {
		mouseHover(locator);
		Actions action = new Actions(webDriver);
		action.doubleClick(this.getElement(locator));
		action.perform();
	}

	public void setImplicitWaitOnDriver(int maxWaitTime) {
		this.webDriver.manage().timeouts().implicitlyWait(maxWaitTime, TimeUnit.SECONDS);
	}

	public void clickUsingJavaScript(By locator) throws Exception {
		try {
			// common.log("clickUsingJavaScript: " + locator);
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			WebElement element = waitForElementToBePresent(locator);
			js.executeScript("arguments[0].click();", element);
			// common.log("clicked!!");
		} catch (StaleElementReferenceException e) {
			Thread.sleep(2000);
			if (isElementPresent(locator, 7)) {
				clickUsingJavaScript(locator);
			}
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public void clickUsingJavaScript(WebElement element) throws Exception {
		try {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript("arguments[0].click();", element);
		} catch (Throwable t) {
			throw new Exception(element + " is missing : " + t);
		}
	}

	public void clickUsingActionApi(By locator) throws Exception {
		mouseHover(locator);
		try {
			Actions actions = new Actions(webDriver);
			actions.moveToElement(waitForElementToBeClickable(locator)).click().perform();
		} catch (Throwable t) {
			throw new Exception(t);
		}
	}

	public String getText(By locator) throws TimeoutException, Exception {
		try {
			return waitForElementToBePresent(locator).getText().trim();
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			getText(locator);
		}
		return null;
	}

	public String getTextWithoutSpecialCharacters(By locator) throws Exception {
		try {
			return getElement(locator).getText().replaceAll("[*$,%/\\(\\)]", "").trim();
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			getTextWithoutSpecialCharacters(locator);
		}
		return null;
	}

	public String getInputBoxValue(By locator) throws Exception {
		try {
			return getElement(locator).getAttribute("value").trim();
		} catch (StaleElementReferenceException s) {
			common.log("StaleElementReferenceException occurred : " + s.getMessage());
			Thread.sleep(2000);
			getInputBoxValue(locator);
		}
		return null;
	}

	public void waitForAngularRequestsToFinish() throws Exception {
		try {
			String url = webDriver.getCurrentUrl();
			if (url.contains("members.wellfit") | url.contains("providers.wellfit") | url.contains("admin.wellfit")) {
				// common.log("waitForAngularRequestsToFinish!!");
				waitUntilAxngularFinishHttpCalls();
				// getNgDriver().waitForAngularRequestsToFinish();
				// ajaxComplete();
				waitForPageToLoad();
				// common.log("Finished!!");
			}
			if (!(getBrowserName().toLowerCase().contains(Browser.IOS.toString().toLowerCase())
					|| getBrowserName().toLowerCase().contains(Browser.ANDROID.toString().toLowerCase())
					|| getBrowserName().toLowerCase().contains(Browser.SAFARI.toString().toLowerCase()))) {
				waitForUILoadingIconToDisappear();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if (e.getMessage().toLowerCase().contains("waiting for element to no longer be visible".toLowerCase())) {
				throw new Exception("Issue : Application keeps on loading. " + e.getMessage());
			}
			common.log("Ignoring error occurred while waiting for loading to finish");
		}
	}

	private void waitUntilAxngularFinishHttpCalls() {
		// Following code not working for Angular 5

//		final String javaScriptToLoadAngular = "var injector = window.angular.element('body').injector();"
//				+ "var $http = injector.get('$http');" + "return ($http.pendingRequests.length === 0)";
//		ExpectedCondition<Boolean> pendingHttpCallsCondition = new ExpectedCondition<Boolean>() {
//			public Boolean apply(WebDriver driver) {
//				return ((JavascriptExecutor) driver).executeScript(javaScriptToLoadAngular).equals(true);
//			}
//		};
//		int timeOut = 50;
//		if (getBrowserName().toLowerCase().contains(Browser.SAFARI.toString().toLowerCase())) {
//			timeOut = 20;
//		}
//		WebDriverWait wait = new WebDriverWait(webDriver, timeOut);
//		wait.until(pendingHttpCallsCondition);
	}

	void waitForPageToLoad() {
		new WebDriverWait(webDriver, 30).until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
				.executeScript("return document.readyState").equals("complete"));
	}

	public void ajaxComplete() {
		String script = "var callback = arguments[arguments.length - 1];" + "var xhr = new XMLHttpRequest();"
				+ "xhr.open('GET', '/Ajax_call', true);" + "xhr.onreadystatechange = function() {"
				+ "  if (xhr.readyState == 4) {" + "    callback(xhr.responseText);" + "  }" + "};" + "xhr.send();";
		ExpectedCondition<Boolean> pendingHttpCallsCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(script).equals(true);
			}
		};
		WebDriverWait wait = new WebDriverWait(webDriver, 60);
		wait.until(pendingHttpCallsCondition);
	}

	public void waitForUILoadingIconToDisappear() throws TimeoutException, Exception {
		String url = webDriver.getCurrentUrl();
		if (url.contains("members.wellfit") | url.contains("providers.wellfit") | url.contains("admin.wellfit")) {
			By locator = By.xpath("//img[@class='loading-icon']");
			try {
				setImplicitWaitOnDriver(0);
				WebDriverWait wait = new WebDriverWait(webDriver, 40);
				wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
			} catch (Exception e) {
				throw new Exception(e);
			} finally {
				setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
			}
		}
	}

	// *[contains(text(),'Loading..')]
	public void selectCheckBox(By locator) throws Exception {
		for (int i = 0; i < 5; i++) {
			if (!getElement(locator).isSelected()) {
				// common.log("selecting check box ");
				click(locator);
			}
			if (getElement(locator).isSelected()) {
				break;
			}
			Thread.sleep(2000);
		}

	}

	public void deSelectCheckBox(By locator) throws Exception {
		if (getElement(locator).isSelected()) {
			// common.log("De selecting check box");
			click(locator);
		}
	}

	public String getAbsoluteXPath(WebElement element) {
		return (String) ((JavascriptExecutor) webDriver)
				.executeScript("function absoluteXPath(element) {" + "var comp, comps = [];" + "var parent = null;"
						+ "var xpath = '';" + "var getPos = function(element) {" + "var position = 1, curNode;"
						+ "if (element.nodeType == Node.ATTRIBUTE_NODE) {" + "return null;" + "}"
						+ "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling){"
						+ "if (curNode.nodeName == element.nodeName) {" + "++position;" + "}" + "}" + "return position;"
						+ "};" +

						"if (element instanceof Document) {" + "return '/';" + "}" +

						"for (; element && !(element instanceof Document); element = element.nodeType ==Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {"
						+ "comp = comps[comps.length] = {};" + "switch (element.nodeType) {" + "case Node.TEXT_NODE:"
						+ "comp.name = 'text()';" + "break;" + "case Node.ATTRIBUTE_NODE:"
						+ "comp.name = '@' + element.nodeName;" + "break;" + "case Node.PROCESSING_INSTRUCTION_NODE:"
						+ "comp.name = 'processing-instruction()';" + "break;" + "case Node.COMMENT_NODE:"
						+ "comp.name = 'comment()';" + "break;" + "case Node.ELEMENT_NODE:"
						+ "comp.name = element.nodeName;" + "break;" + "}" + "comp.position = getPos(element);" + "}" +

						"for (var i = comps.length - 1; i >= 0; i--) {" + "comp = comps[i];"
						+ "xpath += '/' + comp.name.toLowerCase();" + "if (comp.position !== null) {"
						+ "xpath += '[' + comp.position + ']';" + "}" + "}" +

						"return xpath;" +

						"} return absoluteXPath(arguments[0]);", element);
	}

	public void refreshBrowser() {
		// common.log("Refreshing Browser !!");
		try {
			webDriver.navigate().refresh();
			handleAlertIfPresent();
		} catch (Throwable t) {
			handleAlertIfPresent();
		}
	}

	public void refreshBrowserUntilElementPresent(By locator) throws TimeoutException, Exception {
		// common.log("Refresh browser until element present in DOM : " + locator);
		for (int i = 0; i < 7; i++) {
			if (isElementPresent(locator, 2)) {
				break;
			}
			refreshBrowser();
			Thread.sleep(4000);
		}
	}

	public void handleAlertIfPresent() {
		// common.log("Handling Alert if present");
		try {
//			webDriver.navigate().refresh(); // to get rid of the annoying 3 page notification on provider
			Alert alert = webDriver.switchTo().alert();
			alert.accept();
			// common.log("Alert handled");
		} catch (Throwable t) {
			common.log("Alert is not there");
		}
	}

	public void handleAlert() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			try {
				Alert alert = webDriver.switchTo().alert();
				alert.accept();
				// common.log("Alert handled");
				break;
			} catch (Throwable t) {
				common.log("Alert is not there");
			}
			Thread.sleep(2000);
		}
	}

	@Override
	public Capabilities getCapabilities() {
		return webDriver.getCapabilities();
	}

	public WebElement findElement(By locator) throws Exception {
		return webDriver.findElement(locator);
	}

	public List<WebElement> findElements(By locator) throws Exception {
		return webDriver.findElements(locator);
	}

	public void close() {
		webDriver.close();
	}

	public TargetLocator switchTo() {
		return webDriver.switchTo();
	}

	public Object executeJavaScript(String param) {
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		return js.executeScript(param);
	}

	public void executeJavaScript(String param, WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript(param, element);
	}

	public void scrollElementIntoView(WebElement element) {
		executeJavaScript("arguments[0].scrollIntoView(true);", element);
	}

	public Options manage() {
		return webDriver.manage();
	}

	@Override
	public void deletecookies() {
		webDriver.manage().deleteAllCookies();

	}

	@Override
	public String getCurrentUrl() {
		return webDriver.getCurrentUrl();
	}

	@Override
	public WebDriverWait setWebDriverWait(int waitTimeInSec) {
		WebDriverWait wait = new WebDriverWait(webDriver, waitTimeInSec);
		return wait;
	}

	@Override
	public Navigation navigate() {
		return webDriver.navigate();
	}

	@Override
	public String getTitle() {
		return webDriver.getTitle();
	}

	@Override
	public JavascriptExecutor getJavaScriptExecutor() {
		return (JavascriptExecutor) webDriver;
	}

	public void takeScreenShot(String filePath) {
		try {
			Augmenter augmenter = new Augmenter();
			TakesScreenshot ts = (TakesScreenshot) augmenter.augment(webDriver);
			File scrFile = ts.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(filePath));
		} catch (Throwable e) {
			common.log("Screen shot failure " + e.getMessage());
		}
	}

	public Actions getActionsInstance() {
		return new Actions(webDriver);
	}

	@Override
	public SessionId getSessionId() {
		return webDriver.getSessionId();
	}

	public WebDriverWait getWebDriverWaitObject(int timeOut) {
		return new WebDriverWait(webDriver, timeOut);
	}

	@SuppressWarnings("deprecation")
	public Keyboard getKeyboard() {
		return webDriver.getKeyboard();
	}

	@SuppressWarnings("deprecation")
	public Mouse getMouse() {
		return webDriver.getMouse();
	}

	public String getPageSource() {
		return webDriver.getPageSource();
	}

	public String getWindowHandle() {
		return webDriver.getWindowHandle();
	}

	@Override
	public void switchToFrame(String frame) {
		setImplicitWaitOnDriver(0);
		new WebDriverWait(webDriver, 30).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
	}

	@Override
	public void scrollToWebElement(By locator) throws Exception {
		// TODO Auto-generated method stub

	}

}

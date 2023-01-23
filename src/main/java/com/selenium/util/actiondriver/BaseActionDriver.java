package com.selenium.util.actiondriver;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.selenium.util.enums.BaseAppUrl;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public abstract class BaseActionDriver {
	protected RemoteWebDriver webDriver;
	protected AppiumDriver<WebElement> appiumDriver;

	private String browserName;
	private String testCaseName;

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public void setAppiumDriver(IOSDriver<WebElement> appiumDriver) {
		this.appiumDriver = appiumDriver;
	}

	public void setAppiumDriver(AndroidDriver<WebElement> appiumDriver) {
		this.appiumDriver = appiumDriver;
	}

	public void setDriver(RemoteWebDriver driver) {
		this.webDriver = driver;
	}

	public RemoteWebDriver getWebDriver() {
		return webDriver;
	}

	public void setWebDriver(RemoteWebDriver webDriver) {
		this.webDriver = webDriver;
	}

	public AppiumDriver<WebElement> getAppiumDriver() {
		return appiumDriver;
	}

	public void setAppiumDriver(AppiumDriver<WebElement> appiumDriver) {
		this.appiumDriver = appiumDriver;
	}

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public abstract SessionId getSessionId();

	public abstract void setImplicitWaitOnDriver(int maxWaitTime);

	public abstract void refreshBrowserUntilElementPresent(By locator) throws TimeoutException, Exception;

	public abstract String getAbsoluteXPath(WebElement element);

	public abstract void initializeLogging();

	public abstract void handleAlertIfPresent();

	public abstract void handleAlert() throws Exception;

	public abstract void takeScreenShot(String filePath);

	public abstract JavascriptExecutor getJavaScriptExecutor();

	public abstract void click(By locator) throws Exception;

	public abstract void click(String text) throws Exception;

	public abstract void clickAndWaitForElementToDisappear(By locator, int timeOut) throws Exception;

	public abstract void clickAndWaitForNextElementToBePresent(By clickLocator, By nextLocatorToBePresent)
			throws Exception;

	public abstract void clickAndWaitForNextElementToBeVisible(By clickLocator, By locatorToDsiplay) throws Exception;

	public abstract void clickUsingActionApi(By locator) throws Exception;

	public abstract void clickUsingJavaScript(By locator) throws Exception;

	public abstract void clickUsingJavaScript(WebElement element) throws Exception;

	public abstract void clickUsingJSAndWaitForElementToDisappear(By locator, int timeOut) throws Exception;

	public abstract void closeBrowser();

	public abstract void deSelectAll(By locator) throws Exception;

	public abstract void deSelectCheckBox(By locator) throws Exception;

	public abstract void doubleClick(By locator) throws Exception;

	public abstract void get(BaseAppUrl appUrl) throws Exception;

	public abstract void getWithoutMaximizeBrowser(String Url) throws Exception;

	public abstract WebElement getClickableElement(By locator) throws Exception;

	public abstract WebElement getElement(By locator) throws Exception;

	public abstract List<WebElement> getElements(By locator, int waitTime) throws Exception;

	public abstract void waitForAngularRequestsToFinish() throws Exception;

	public abstract <T> WebElement waitForElementToBePresent(final T locator) throws Exception;

	public abstract <T> WebElement waitForElementToBePresent(T locator, int i) throws Exception;

	public abstract WebElement waitForElementToBeClickable(By locator, int timeOut) throws Exception;

	public abstract WebElement waitForElementToBeClickable(By locator) throws Exception;

	public abstract String getTextWithoutSpecialCharacters(By locator) throws Exception;

	public abstract boolean isElementVisible(By locator, int waitTime) throws Exception;

	public abstract void type(By locator, CharSequence... testData) throws Exception;

	public abstract void typeUsingActionApi(By locator, String testData) throws Exception;

	public abstract void typeUsingJavaScript(By locator, String testData) throws Exception;

	public abstract void blurUsingJavaScript(final By locator) throws Exception;

	public abstract void clearTextBoxValue(By locator) throws Exception;

	public abstract void selectByValue(By locator, String testData) throws Exception;

	public abstract <T> WebElement waitForElementToBeVisible(final T locator) throws Exception;

	public abstract <T> WebElement waitForElementToBeVisible(final T locator, int timeOut) throws Exception;

	public abstract boolean isElementPresent(By locator, int waitTime) throws Exception;

	public abstract void deletecookies();

	public abstract void selectByText(By dropdown, String text) throws Exception;

	public abstract String getInputBoxValue(By locator) throws Exception;

	public abstract void waitUntilElementDisappear(By locator, int i) throws Exception;

	public abstract String getText(By locator) throws Exception;

	public abstract void mouseHover(By locator) throws Exception;

	public abstract String getFirstSelectedOption(By locator) throws Exception;

	public abstract boolean isElementVisible(String text, int waitTime) throws Exception;

	public abstract boolean isElementVisible(By locator) throws Exception;

	public abstract void scrollUp();

	public abstract void scrollDown();

	public abstract void scrollToWebElement(By locator) throws Exception;

	public abstract void selectCheckBox(By locator) throws Exception;

	public abstract void selectByIndex(By xpath, int i) throws Exception;

	public abstract boolean isOptionExistInDropDown(By locator, String option) throws Exception;

	public abstract void maximizeBrowser();

	public abstract Object executeJavaScript(String param);

	public abstract void executeJavaScript(String param, WebElement element);

	public abstract void scrollElementIntoView(WebElement element);

	public abstract void refreshBrowser();

	public abstract WebDriverWait setWebDriverWait(int waitTimeInSec);

	public abstract List<WebElement> getDropdownOptions(By locator) throws Exception;

	public abstract <T> void waitForElementNotPresent(T locator, int waitTime) throws Exception, TimeoutException;

	public abstract Actions getActionsInstance();

	public abstract WebDriverWait getWebDriverWaitObject(int timeOut);

	// selenium default functions
	public abstract Options manage();

	public abstract WebElement findElement(By locator) throws Exception;

	public abstract List<WebElement> findElements(By locator) throws Exception;

	public abstract void get(String Url) throws Exception;

	public abstract void close();

	public abstract void quit();

	public abstract Navigation navigate();

	public abstract String getTitle();

	public abstract Set<String> getWindowHandles();

	public abstract String getWindowHandle();

	public abstract Capabilities getCapabilities();

	public abstract void sendKeys(By locator, Object testData) throws Exception;

	public abstract String getCurrentUrl();

	public abstract TargetLocator switchTo();

	@SuppressWarnings("deprecation")
	public abstract Keyboard getKeyboard();

	@SuppressWarnings("deprecation")
	public abstract Mouse getMouse();

	public abstract String getPageSource();

	public abstract void switchToFrame(String frame);

}

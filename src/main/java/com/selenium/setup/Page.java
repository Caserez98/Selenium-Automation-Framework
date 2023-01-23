package com.selenium.setup;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

/**
 * Page utilities
 * 
 */
public class Page {
	static final Logger log = getLogger(lookup().lookupClass());
	public static final int PAGE_TIMEOUT = 15;

	private Page() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Waits for JS on page to in ready state. If page has JS to load, use this
	 * method for page load
	 * 
	 * @param driver
	 * @param timeout
	 */
	public static void waitForJSLoad(WebDriver driver, int timeout) {
		new WebDriverWait(driver, timeout).until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
				.executeScript("return document.readyState").equals("complete"));
	}

	/**
	 * Waits for JQuery on page to be active. If page has Jquery on load, use this
	 * method for page load
	 * 
	 * @param driver
	 * @param timeout
	 */
	public static void waitForJQuery(WebDriver driver, int timeout) {
		new WebDriverWait(driver, timeout).until((ExpectedCondition<Boolean>) wd -> ((Boolean) ((JavascriptExecutor) wd)
				.executeScript("return window.jQuery != undefined && jQuery.active === 0")));
	}

	/**
	 * Waits for JS on page to in ready state. Waits for JQuery on page to be
	 * active. If the page has both JQuery and JS on load use this method
	 * 
	 * @param driver
	 * @param timeout
	 */
	public static void waitForJSJQuery(WebDriver driver, int timeout) {
		waitForJSLoad(driver, timeout);
		waitForJQuery(driver, timeout);
	}

	/**
	 * Navigates to url, maximizes the page and switches the context to it.
	 * 
	 * @param driver
	 * @param url
	 * 
	 */
	public static void navigate(WebDriver driver, String url) {
		driver.navigate().to(url);
		driver.manage().window().maximize();
		driver.switchTo().activeElement();
	}

	/**
	 * Switch to header frame with index 0
	 * 
	 * @param driver
	 */
	public static void switchToHeaderFrame(WebDriver driver) {
		SelTestCase.getActionDriver().setImplicitWaitOnDriver(0);
		String banner = "banner";
		By frameLocator = By.xpath("//frame");
		driver.switchTo().defaultContent();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		int frames = driver.findElements(frameLocator).size();
		int inLogin = driver.findElements(By.xpath("//button[@id='btnLogin']")).size();
		if (inLogin != 0 || frames == 0) {
			try {
				new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(frameLocator));
			} catch (TimeoutException ex) {
				log.warn("Frames not available to switch right now...Check and delete extra frame switches if needed");
			}
		}

		new WebDriverWait(driver, 120).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(banner));
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	}

	/**
	 * Switch to body frame with name - main
	 * 
	 * @param driver
	 */
	public static void switchToBodyFrame(WebDriver driver) {
		SelTestCase.getActionDriver().setImplicitWaitOnDriver(0);
		driver.switchTo().defaultContent();
		new WebDriverWait(driver, 30).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("main"));
	}

	/**
	 * Switch to left frame with name - left
	 * 
	 * @param driver
	 */
	public static void switchToLeftFrame(WebDriver driver) {
		SelTestCase.getActionDriver().setImplicitWaitOnDriver(0);
		driver.switchTo().defaultContent();
		new WebDriverWait(driver, 30).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("main"));
		new WebDriverWait(driver, 30).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("left"));
	}

	/**
	 * Switch to right frame with name - right
	 * 
	 * @param driver
	 */
	public static void switchToRightFrame(WebDriver driver) {
		SelTestCase.getActionDriver().setImplicitWaitOnDriver(0);
		driver.switchTo().defaultContent();
		new WebDriverWait(driver, 30).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("main"));
		new WebDriverWait(driver, 30).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("right"));
	}

	/**
	 * isAlertPresent returns true if browser alert is present, otherwise false.
	 * 
	 * @param driver
	 * @return true if browser alert is present, otherwise false.
	 */
	public static boolean isAlertPresent(WebDriver driver) {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException ex) {
			log.warn("Alert not present...");
			return false;
		}
	}

}

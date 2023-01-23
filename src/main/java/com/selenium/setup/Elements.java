package com.selenium.setup;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Element reusable utilities
 * 
 */
public class Elements {
	private Elements() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns optional web element if visible on UI
	 * 
	 * @param driver
	 * @param timeout
	 */
	public static WebElement getVisibleElement(WebDriver driver, int timeout, By locator) {

		new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
		return driver.findElement(locator);
	}

	/**
	 * Returns web element list if visible on UI if available, else returns
	 * Optional.emtpy
	 * 
	 * @param driver
	 * @param timeout
	 */
	public static Optional<List<WebElement>> getVisibleElements(WebDriver driver, int timeout, By locator) {
		if (driver.findElements(locator).isEmpty()) {
			return Optional.empty();
		}
		new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
		return Optional.of(driver.findElements(locator));
	}
}

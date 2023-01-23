package com.computers.sample.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.selenium.setup.Elements;
import com.selenium.setup.Page;

public class WelcomePage {

	private WebDriver driver;

	public WelcomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, Page.PAGE_TIMEOUT), this);
		Page.waitForJSLoad(driver, 30);
	}

	// Sync Object
	By syncObj = By.xpath("//a[@class='fill']");

	public boolean pageLoaded() {
		new WebDriverWait(driver, Page.PAGE_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(syncObj));
		return Elements.getVisibleElement(driver, Page.PAGE_TIMEOUT, syncObj).isDisplayed();
	}

	// Objects
	@FindBy(id = "add")
	WebElement btnAddNewComputer;

	@FindBy(xpath = "//*[@class='alert-message warning']")
	WebElement lblAlertMessageSuccess;

	/*
	 * Click Add a new computers
	 * 
	 * @author: ricardo.avalos
	 * 
	 * @date: 08/08/2022
	 * 
	 * @param: NA
	 */
	public void clickAddComputer() {
		new WebDriverWait(driver, Page.PAGE_TIMEOUT).until(ExpectedConditions.elementToBeClickable(btnAddNewComputer));
		btnAddNewComputer.click();
	}

	/*
	 * Validate computer was created successfully
	 * 
	 * @author: ricardo.avalos
	 * 
	 * @date: 08/08/2022
	 * 
	 * @param: NA
	 */
	public boolean validateComputerCreateSuccessfully() {
		return lblAlertMessageSuccess.isDisplayed();
	}

}

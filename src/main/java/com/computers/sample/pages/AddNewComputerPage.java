package com.computers.sample.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.computers.sample.dao.AddNewComputerData;
import com.selenium.setup.Elements;
import com.selenium.setup.Page;

public class AddNewComputerPage {

	private WebDriver driver;

	public AddNewComputerPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
		Page.waitForJSLoad(driver, 30);
	}

	// Sync Object
	By syncObj = By.id("main");

	public boolean pageLoaded() {
		new WebDriverWait(driver, Page.PAGE_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(syncObj));
		return Elements.getVisibleElement(driver, Page.PAGE_TIMEOUT, syncObj).isDisplayed();
	}

	// Objects
	@FindBy(id = "name")
	WebElement txtComputerName;

	@FindBy(id = "introduced")
	WebElement txtIntroduced;

	@FindBy(id = "discontinued")
	WebElement txtDiscontinued;

	@FindBy(xpath = "//input[@class='btn primary']")
	WebElement btnCreateThisComputer;

	/*
	 * Method add new computer
	 * 
	 * @author: ricardo.avalos
	 * 
	 * @date: 08/08/2022
	 * 
	 * @param: NA
	 */
	public void addNewComputer(AddNewComputerData data) {
		txtComputerName.sendKeys(data.getComputerName());
		txtIntroduced.sendKeys(data.getIntroduced());
		txtDiscontinued.sendKeys(data.getDiscontinued());
		btnCreateThisComputer.click();
	}

}

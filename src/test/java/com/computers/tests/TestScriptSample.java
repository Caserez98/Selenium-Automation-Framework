package com.computers.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.computers.sample.dao.AddNewComputerData;
import com.computers.sample.pages.AddNewComputerPage;
import com.computers.sample.pages.WelcomePage;
//import com.credible.framework.utilities.gson.GsonUtility;
import com.selenium.setup.SelTestCase;
import com.selenium.util.CONFIGKEY;
import com.selenium.util.CommonUtil;
import com.selenium.util.GsonUtility;
import com.selenium.util.RetryAnalyzer;
import com.selenium.util.TestGroups;

public class TestScriptSample extends SelTestCase {

	private String className = this.getClass().getSimpleName();
	private static final String TEST_SCRIPT_SAMPLE_JSON = "/testdata/computersapp/TestScriptSample.json";

	RemoteWebDriver driver;

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		initializeWebTest(className, null);
	}

	/*
	 * Test ID - This is the test case description
	 * 
	 * @author: alexander.caserez
	 */

	@Test(retryAnalyzer = RetryAnalyzer.class, groups = { TestGroups.COMPUTERS, TestGroups.IMPLEMENTED })
	public void ts001AddNewComputer() throws Exception {

		WebDriver driver = SelTestCase.getActionDriver().getWebDriver();
		actionDriver.get(CommonUtil.getKeyValue(CONFIGKEY.APP_URL_SAMPLE));

		// Step 1 Navigate Welcome Page
		WelcomePage welcomePage = new WelcomePage(driver);
		Assert.assertTrue(welcomePage.pageLoaded());
		common.log("Welcome page is loaded successfully");

		// Step 2 Click Add a new computer
		welcomePage.clickAddComputer();
		common.log("Clicked Add computer successfully");

		// Step 3 Fill out the fields to create a new computer
		// Step 4 Click Create this computer
		AddNewComputerPage addnewPage = new AddNewComputerPage(driver);
		Assert.assertTrue(addnewPage.pageLoaded());
		common.log("Add new computer page is loaded successfully");
		AddNewComputerData data = GsonUtility.getObjectFromJsonResource(TEST_SCRIPT_SAMPLE_JSON,
				AddNewComputerData.class);
		addnewPage.addNewComputer(data);
		common.log("Add a new computer was successfully");

		// Step 5 Validate computer was created successfully
		Assert.assertTrue(welcomePage.validateComputerCreateSuccessfully());
		common.log("Validation new computer creation was successfully");
	}

	/*
	 * Test ID - This is the test case description
	 * 
	 * @author: alexander.caserez
	 */

	@Test(retryAnalyzer = RetryAnalyzer.class, groups = { TestGroups.COMPUTERS, TestGroups.IMPLEMENTED })
	public void ts002AddNewComputer() throws Exception {

		WebDriver driver = SelTestCase.getActionDriver().getWebDriver();
		actionDriver.get(CommonUtil.getKeyValue(CONFIGKEY.APP_URL_SAMPLE));

		// Step 1 Navigate Welcome Page
		WelcomePage welcomePage = new WelcomePage(driver);
		Assert.assertTrue(welcomePage.pageLoaded());
		common.log("Welcome page is loaded successfully");

		// Step 2 Click Add a new computer
		welcomePage.clickAddComputer();

		// Step 3 Fill out the fields to create a new computer
		// Step 4 Click Create this computer
		AddNewComputerPage addnewPage = new AddNewComputerPage(driver);
		Assert.assertTrue(addnewPage.pageLoaded());
		common.log("Add new computer page is loaded successfully");
		AddNewComputerData data = GsonUtility.getObjectFromJsonResource(TEST_SCRIPT_SAMPLE_JSON,
				AddNewComputerData.class);
		addnewPage.addNewComputer(data);
		common.log("Add a new computer was successfully");

		// Step 5 Validate computer was created successfully
		Assert.assertTrue(welcomePage.validateComputerCreateSuccessfully());
		common.log("Validation new computer creation was successfully");
	}

	/*
	 * Test ID - This is the test case description
	 * 
	 * @author: alexnader.caserez
	 */

	@Test(retryAnalyzer = RetryAnalyzer.class, groups = { TestGroups.COMPUTERS, TestGroups.IMPLEMENTED })
	public void ts003AddNewComputer() throws Exception {

		WebDriver driver = SelTestCase.getActionDriver().getWebDriver();
		actionDriver.get(CommonUtil.getKeyValue(CONFIGKEY.APP_URL_SAMPLE));

		// Step 1 Navigate Welcome Page
		WelcomePage welcomePage = new WelcomePage(driver);
		Assert.assertTrue(welcomePage.pageLoaded());
		common.log("Welcome page is loaded successfully");

		// Step 2 Click Add a new computer
		welcomePage.clickAddComputer();

		// Step 3 Fill out the fields to create a new computer
		// Step 4 Click Create this computer
		AddNewComputerPage addnewPage = new AddNewComputerPage(driver);
		Assert.assertTrue(addnewPage.pageLoaded());
		common.log("Add new computer page is loaded successfully");
		AddNewComputerData data = GsonUtility.getObjectFromJsonResource(TEST_SCRIPT_SAMPLE_JSON,
				AddNewComputerData.class);
		addnewPage.addNewComputer(data);
		common.log("Add a new computer was successfully");

		// Step 5 Validate computer was created successfully
		Assert.assertTrue(welcomePage.validateComputerCreateSuccessfully());
		common.log("Validation new computer creation was successfully");
	}

	@Override
	protected void cleanPageObjects() {

	}
}
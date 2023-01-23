package com.selenium.setup;

import java.lang.reflect.Method;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.selenium.util.CONFIGKEY;
import com.selenium.util.CommonUtil;
import com.selenium.util.CommonUtil.PropertyFile;
import com.selenium.util.TestNgListeners;
import com.selenium.util.actiondriver.BaseActionDriver;
import com.selenium.util.actiondriver.MobileActionDriver;
import com.selenium.util.actiondriver.WebActionDriver;
import com.selenium.util.enums.BOOLEAN;
import com.selenium.util.enums.BaseAppUrl;
import com.selenium.util.enums.Browser;

@Listeners(TestNgListeners.class)
public abstract class SelTestCase {

	protected abstract void cleanPageObjects();

	public BaseActionDriver actionDriver;
	public CommonUtil common;
	public ExtentTest test;
	public boolean startNewBrowserSession = true;
	public String browserName;
	public String testCaseName;
	public Throwable Error = null;
	public int counter;

	public static int WAIT_TIMEOUT;
	public static ExtentReports reports;

	protected static ThreadLocal<BaseActionDriver> actionDriverThread = new ThreadLocal<BaseActionDriver>();
	protected static ThreadLocal<CommonUtil> commonThread = new ThreadLocal<CommonUtil>();
	protected static ThreadLocal<ExtentTest> testThread = new ThreadLocal<ExtentTest>();

	private String testId;
	private BaseAppUrl appUrl;

	public static ExtentTest getTest() {
		return testThread.get();
	}

	public static CommonUtil getCommon() {
		return commonThread.get();
	}

	public static BaseActionDriver getActionDriver() {
		return actionDriverThread.get();
	}

	/**
	 * setUp function will invoke by TestNg before execution of every test case. It
	 * initializes the property files and html report setup
	 * 
	 * @throws Exception
	 */
	@BeforeMethod(groups = { "smoke", "prod" }, alwaysRun = true)
	@Parameters({ "browser" })
	public synchronized void setUp(@Optional String browser, Method method) throws Exception {
		try {
			if (browser == null) {
				browserName = CommonUtil.getKeyValue(CONFIGKEY.BROWSER_TO_TEST);
			} else {
				browserName = browser;
			}
			testCaseName = method.getName();
			actionDriver.setBrowserName(browserName);
			actionDriver.setTestCaseName(testCaseName);
			testThread.set(reports.startTest(browserName + " " + common.testCaseId + "-" + testCaseName, ""));
			test = testThread.get();
			common.initializeLogs(test);
			counter = counter + 1;
			if (actionDriver.getBrowserName().contains(Browser.ANDROID.toString())
					|| actionDriver.getBrowserName().contains(Browser.IOS.toString())) {
				if (getActionDriver().getClass().getName().contains("WebActionDriver")) {
					if (CommonUtil
							.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_CROSSBROWSERTESTING, PropertyFile.MOBILE_CONFIG)
							.equalsIgnoreCase(BOOLEAN.TRUE.toString())
							| CommonUtil
									.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_SAUCELAB, PropertyFile.MOBILE_CONFIG)
									.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
						switchToAppiumDriver();
					} else {
						switchToRemoteWebDriver(browserName);
					}
				} else {
					// initialize Appium driver
					common.initializeDriver(browserName);
				}
			} else {
				common.initializeDriver(browserName);
			}

		} catch (Throwable t) {
			t.printStackTrace();
			if (actionDriver.getAppiumDriver() != null) {
				actionDriver.quit();
			}
			if (actionDriver.getWebDriver() != null) {
				actionDriver.quit();
			}
			throw new SkipException("Skipping test case due to following error : " + t.getMessage());
		}

	}

	/**
	 * tearDown function will invoke by TestNg after execution of every test case.
	 * It adds the test case status into the html report .
	 * 
	 * @throws Exception
	 */
	@AfterMethod(alwaysRun = true)
	public synchronized void tearDown() {
		reports.flush();
		actionDriver.closeBrowser();
	}

	public void initializeWebTest(String testId, BaseAppUrl appUrl) throws Exception {
		actionDriverThread.set(new WebActionDriver());
		commonThread.set(new CommonUtil(getActionDriver()));
		getActionDriver().initializeLogging();
		getCommon().testCaseId = testId;
		getCommon().appUrl = appUrl;
		this.testId = testId;
		this.appUrl = appUrl;
		CommonUtil.setInitialConfigurations();
		browserName = CommonUtil.getKeyValue(CONFIGKEY.BROWSER_TO_TEST);
		this.common = getCommon();
		this.actionDriver = getActionDriver();
	}

	public void initializeMobileTest(String testId, BaseAppUrl appUrl) throws Exception {
		actionDriverThread.set(new MobileActionDriver());
		commonThread.set(new CommonUtil(getActionDriver()));
		getActionDriver().initializeLogging();
		getCommon().testCaseId = testId;
		getCommon().appUrl = appUrl;
		this.testId = testId;
		this.appUrl = appUrl;
		CommonUtil.setInitialConfigurations();
		browserName = CommonUtil.getKeyValue(CONFIGKEY.BROWSER_TO_TEST);
		this.common = getCommon();
		this.actionDriver = getActionDriver();
		if (!(CommonUtil.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_CROSSBROWSERTESTING, PropertyFile.MOBILE_CONFIG)
				.equalsIgnoreCase(BOOLEAN.TRUE.toString())
				| CommonUtil.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_SAUCELAB, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString()))) {
			switchToRemoteWebDriver(browserName);
		}
	}

//	public void switchToRemoteWebDriver() {
//		common.log("Switching to Remote Web Driver");
//		actionDriver.closeBrowser();
//		cleanPageObjects();
//		actionDriverThread.remove();
//		commonThread.remove();
//		actionDriverThread.set(new WebActionDriver());
//		commonThread.set(new CommonUtil(getActionDriver()));
//		getActionDriver().initializeLogging();
//		getCommon().testCaseId = testId;
//		getCommon().appUrl = appUrl;
//		this.common = getCommon();
//		this.actionDriver = getActionDriver();
//		actionDriver.setBrowserName(browserName);
//		actionDriver.setTestCaseName(testCaseName);
//		common.initializeLogs(test);
//	}

	public void switchToRemoteWebDriver(Browser browser) throws Exception {
		switchToRemoteWebDriver(browser.toString());
	}

	public void switchToRemoteWebDriver(String browser) throws Exception {
		closeBrowser();
		actionDriverThread.remove();
		commonThread.remove();
		actionDriverThread.set(new WebActionDriver());
		commonThread.set(new CommonUtil(getActionDriver()));
		getActionDriver().initializeLogging();
		getCommon().testCaseId = testId;
		getCommon().appUrl = appUrl;
		this.common = getCommon();
		this.actionDriver = getActionDriver();
		actionDriver.setBrowserName(browserName);
		actionDriver.setTestCaseName(testCaseName);
		common.initializeLogs(test);
		common.initializeDriver(browser);
	}

	public void switchToAppiumDriver() throws Exception {
		closeBrowser();
		actionDriverThread.remove();
		commonThread.remove();
		actionDriverThread.set(new MobileActionDriver());
		commonThread.set(new CommonUtil(getActionDriver()));
		getActionDriver().initializeLogging();
		getCommon().testCaseId = testId;
		getCommon().appUrl = appUrl;
		this.common = getCommon();
		this.actionDriver = getActionDriver();
		actionDriver.setBrowserName(browserName);
		actionDriver.setTestCaseName(testCaseName);
		common.initializeLogs(test);
		if (!(CommonUtil.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_CROSSBROWSERTESTING, PropertyFile.MOBILE_CONFIG)
				.equalsIgnoreCase(BOOLEAN.TRUE.toString())
				|| CommonUtil.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_SAUCELAB, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString()))) {
			switchToRemoteWebDriver(browserName);
		} else {
			common.initializeDriver(browserName);
		}
	}

	public void closeBrowser() {
		actionDriver.closeBrowser();
		cleanPageObjects();
	}

}

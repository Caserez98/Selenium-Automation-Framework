package com.selenium.util;

import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.Assert;
import org.testng.Reporter;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.selenium.mobile.util.Device;
import com.selenium.mobile.util.DeviceType;
import com.selenium.setup.SelTestCase;
import com.selenium.testdata.RandomUtil;
import com.selenium.util.actiondriver.BaseActionDriver;
import com.selenium.util.enums.BOOLEAN;
import com.selenium.util.enums.BaseAppUrl;
import com.selenium.util.enums.Browser;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.github.bonigarcia.wdm.WebDriverManager;

public class CommonUtil {

	private ChromeOptions chromeOptions;
	private InternetExplorerOptions ieOptions;
	private FirefoxOptions firefoxOption;
	private SafariOptions safariOptions;
	private BaseActionDriver actionDriver;
	private String sessionId;

	public static String MAIN_DIR;
	public static FileInputStream fn;
	public static Properties TEST_ENV;
	public static Properties CONFIG;
	public static Properties MOBILE_CONFIG;
	public static boolean isInitialConfigurationDone = false;
	public static boolean isReportInitialized = false;
	public static String ENVIRONMENT_UNDER_TEST;

	public Properties dataBaseTestData;
	public int testCaseRowNum;
	public String testStatusDescription;
	public String screenShotName = null;
	public String testCaseId = null;
	public BaseAppUrl appUrl;
	public ExtentTest test;
	public String browserName;

	public CommonUtil(BaseActionDriver actionDriver) {
		this.actionDriver = actionDriver;
	}

	public CommonUtil() {

	}

	public synchronized static void setInitialConfigurations() throws Exception {
		if (!isInitialConfigurationDone) {
			isInitialConfigurationDone = true;
			System.out.println("Initializing Selenium files path");
			try {
				TEST_ENV = new Properties();
				CONFIG = new Properties();
				String baseFilePath = System.getProperty("user.dir") + "/src/main/resources/config/";
				String filePath = null;

				fn = new FileInputStream(baseFilePath + "testEnv.properties");
				TEST_ENV.load(fn);

				if (TEST_ENV.getProperty("testEnv").equalsIgnoreCase("QA")) {
					filePath = baseFilePath + "config.properties";
				}

				// Load web Config
				fn = new FileInputStream(filePath);
				CONFIG.load(fn);

				/*
				 * XLS_READER = new Xls_Reader( System.getProperty("user.dir") +
				 * "//src//com//selenium//testdata//Suite.xlsx");
				 */
				// set the max wait time
				SelTestCase.WAIT_TIMEOUT = Integer.parseInt(CONFIG.getProperty("WAIT_TIMEOUT"));
			} catch (Exception e) {
				throw new Exception("Environment configuration is not set " + e.getMessage());
			} finally {
				isInitialConfigurationDone = true;
			}

		}
		if (!isReportInitialized) {
			isReportInitialized = true;
			// reports cleaning
			CommonUtil.cleanOldReports();
			System.out.println("Setting up extent Report object");
			SelTestCase.reports = new ExtentReports("./AutomationReport/" + "/index.html");
		}
	}

	public void initializeDriver(Browser browser) throws Exception {
		initializeDriver(browser.toString());
	}

	/**
	 * It initialize the webdriver object with either firefox/chrome/IE
	 *
	 * @throws Exception
	 */
	public void initializeDriver(String browser) throws Exception {
		log("Initializing driver !!");
		String deviceName;
		try {
			browserName = browser;
			actionDriver.setBrowserName(browser);
			if (browser.equalsIgnoreCase(Browser.FIREFOX.toString())) {
				firefoxOption = new FirefoxOptions();
				firefoxOption.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
				firefoxOption.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				if (CommonUtil.CONFIG.getProperty(CONFIGKEY.RUN_WEB_SCRIPT_ON_LOCAL)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					// firefoxOption.setCapability(FirefoxDriver.PROFILE, getFirefoxProfile());
					// setFirefoxDriverPath();
					WebDriverManager.firefoxdriver().setup();
					actionDriver.setDriver(new FirefoxDriver(firefoxOption));
					log("Firefox driver initialized !!");
				} else {
					actionDriver
							.setDriver(new RemoteWebDriver(new URL(getKeyValue(CONFIGKEY.WEB_HUB_URL)), firefoxOption));
					log("RemoteWebDriver initialized for Firefox Browser !!");
				}
			} else if (browser.equalsIgnoreCase(Browser.IE.toString())) {
				ieOptions = new InternetExplorerOptions();
				ieOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.WINDOWS);
				ieOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				ieOptions.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true);
				ieOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				ieOptions.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
				ieOptions.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				ieOptions.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
				if (CommonUtil.CONFIG.getProperty(CONFIGKEY.RUN_WEB_SCRIPT_ON_LOCAL)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					// setIEDriverPath();
					// WebDriverManager.iedriver().arch32().proxy("server:port").setup();
					WebDriverManager.iedriver().arch32().setup();
					actionDriver.setDriver(new InternetExplorerDriver(ieOptions));
					log("IE driver initialized !!");
				} else {
					actionDriver.setDriver(new RemoteWebDriver(new URL(getKeyValue(CONFIGKEY.WEB_HUB_URL)), ieOptions));
					log("RemoteWebDriver initialized for IE !!");
				}
			} else if (browser.equalsIgnoreCase(Browser.CHROME.toString())) {
				chromeOptions = new ChromeOptions();
				chromeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, "accept");
				chromeOptions.addArguments("disable-extensions");
				chromeOptions.addArguments("--start-maximized");
				if (CommonUtil.CONFIG.getProperty(CONFIGKEY.RUN_WEB_SCRIPT_ON_LOCAL)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					WebDriverManager.chromedriver().setup();
//					setChromeDriverPath();
					// WebDriverManager.chromedriver().clearPreferences();
					// WebDriverManager.chromedriver().version("80.0.3987").setup();
					// WebDriverManager.chromedriver().version("79.0.3945").setup();
					// WebDriverManager.chromedriver().proxy("server:port").setup();
					actionDriver.setDriver(new ChromeDriver(chromeOptions));
					log("Chrome driver initialized !!");
				} else {
					actionDriver
							.setDriver(new RemoteWebDriver(new URL(getKeyValue(CONFIGKEY.WEB_HUB_URL)), chromeOptions));
					log("RemoteWebDriver initialized for Chrome  !!");
				}

			} else if (browser.toLowerCase().contains(Browser.SAFARI.toString().toLowerCase())) {
				safariOptions = new SafariOptions();
				String ver = browser.substring(browser.length() - 1);
				if (ver.equals("8") | ver.equals("9")) {
					safariOptions.setUseTechnologyPreview(true);
					safariOptions.setCapability(CapabilityType.VERSION, ver);
				} else {
					safariOptions.setCapability(CapabilityType.VERSION,
							Device.getSafariVersionForCrossBrowserTesting());
				}
				safariOptions.setCapability(CapabilityType.BROWSER_NAME, Browser.SAFARI.toString());
				// Cross Browser testing capabilities
				safariOptions.setCapability("name", browser + "_" + actionDriver.getTestCaseName());
				safariOptions.setCapability("record_video", "true");
				safariOptions.setCapability("record_network", "false");
				// safariOptions.setCapability("max_duration", 360);
				String URL = "http://"
						+ CommonUtil.getKeyValue(CONFIGKEY.CROSSBROWSERTESTING_USERNAME, PropertyFile.MOBILE_CONFIG)
						+ ":"
						+ CommonUtil.getKeyValue(CONFIGKEY.CROSSBROWSERTESTING_AUTOMATE_KEY, PropertyFile.MOBILE_CONFIG)
						+ "@hub.crossbrowsertesting.com:80/wd/hub";
				log("Cross Browser testing HUB URL : " + URL);
				actionDriver.setDriver(new RemoteWebDriver(new URL(URL), safariOptions));
				log("Safari driver initialized on Cross Browser Testing tool !!");
			} else if (browser.toLowerCase().contains(Browser.IOS.toString().toLowerCase())) {
				safariOptions = new SafariOptions();
				safariOptions.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 360);
				safariOptions.setCapability("autoAcceptAlerts", true);
				if (CommonUtil.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_SAUCELAB, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase("true")) {
					safariOptions.setCapability("testobject_api_key",
							CommonUtil.getKeyValue(CONFIGKEY.TEST_OBJECT_API_KEY, PropertyFile.MOBILE_CONFIG));
					safariOptions.setCapability(MobileCapabilityType.PLATFORM_NAME, "IOS");
					safariOptions.setCapability("name", actionDriver.getTestCaseName());
					safariOptions.setCapability("testobject_test_live_view_url", true);
					safariOptions.setCapability("testobject_test_report_url", true);
					safariOptions.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");

					if (browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
						safariOptions.setCapability(MobileCapabilityType.DEVICE_NAME, "iPad .*");
					} else {
						safariOptions.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone .*");
					}
					safariOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
					actionDriver.setAppiumDriver(new IOSDriver<WebElement>(new URL(
							CommonUtil.getKeyValue(CONFIGKEY.SAUCELAB_APPIUM_HUB_URL, PropertyFile.MOBILE_CONFIG)),
							safariOptions));
					log("IOS driver initialized on sauce lab!!");
				} else if (CommonUtil
						.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_BROWSERSTACK, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase("true")) {
					String URL = "https://"
							+ CommonUtil.getKeyValue(CONFIGKEY.BROWSERSTACK_USERNAME, PropertyFile.MOBILE_CONFIG) + ":"
							+ CommonUtil.getKeyValue(CONFIGKEY.BROWSERSTACK_AUTOMATE_KEY, PropertyFile.MOBILE_CONFIG)
							+ "@hub-cloud.browserstack.com/wd/hub";
					if (browserName.toLowerCase().contains("tablet")) {
						deviceName = Device.getIpadNameForBrowserStack();
						safariOptions.setCapability("device", deviceName);
					} else {
						deviceName = Device.getIphoneNameForBrowserStack();
						safariOptions.setCapability("device", deviceName);
					}
					safariOptions.setCapability("browserstack.debug", "true");
					safariOptions.setCapability("realMobile", "true");
					actionDriver.setDriver(new RemoteWebDriver(new URL(URL), safariOptions));
					log("IOS driver initialized on browser stack !!");
					log("Device Name : " + deviceName);
				} else if (CommonUtil
						.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_CROSSBROWSERTESTING, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					String URL = "http://"
							+ CommonUtil.getKeyValue(CONFIGKEY.CROSSBROWSERTESTING_USERNAME, PropertyFile.MOBILE_CONFIG)
							+ ":" + CommonUtil.getKeyValue(CONFIGKEY.CROSSBROWSERTESTING_AUTOMATE_KEY,
									PropertyFile.MOBILE_CONFIG)
							+ "@hub.crossbrowsertesting.com:80/wd/hub";
					log("Cross Browser testing HUB URL : " + URL);
					if (browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
						deviceName = Device.getIpadNameForCrossBrowserTesting();
						safariOptions.setCapability("deviceName", deviceName);
					} else {
						deviceName = Device.getIphoneNameForCrossBrowserTesting();
						safariOptions.setCapability("deviceName", deviceName);
					}
					safariOptions.setCapability("name", deviceName + "_" + actionDriver.getTestCaseName());
					safariOptions.setCapability("record_video", true);
					safariOptions.setCapability("record_network", false);
					// Max test length should be not more than 10 minutes
					safariOptions.setCapability("max_duration", 600);
					actionDriver.setAppiumDriver(new IOSDriver<WebElement>(new URL(URL), safariOptions));
					log("IOS driver initialized on cross browser testing cloud !!");
					log("Device Name : " + deviceName);
				} else {
					if (getKeyValue(CONFIGKEY.RUN_APPIUM_SCRIPT_IN_MOBILE_MODE_ON_DESKTOP, PropertyFile.MOBILE_CONFIG)
							.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
						if (browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
							initializeTabletModeInDesktopChromeBrowser();
						} else {
							initializeMobileModeInDesktopChromeBrowser();
						}
					} else if (getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_LOCAL_IPHONE_REAL_DEVICE,
							PropertyFile.MOBILE_CONFIG).equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
						safariOptions.setCapability("startIWDP", true);
						safariOptions.setCapability("platformName", "iOS");
						safariOptions.setCapability("deviceName",
								getKeyValue(CONFIGKEY.IOS_DEVICE_NAME, PropertyFile.MOBILE_CONFIG));
						safariOptions.setCapability("automationName", "XCUITest");
						safariOptions.setCapability("browserName", "Safari");
						safariOptions.setCapability("launchTimeout", 120000);
						if (!getKeyValue(CONFIGKEY.IOS_DEVICE_NAME, PropertyFile.MOBILE_CONFIG).toLowerCase()
								.contains("simulator")) {
							safariOptions.setCapability("platformVersion", "10.3");
							safariOptions.setCapability("udid",
									CommonUtil.getKeyValue(CONFIGKEY.IOS_DEVICE_UDID, PropertyFile.MOBILE_CONFIG));
						}
						actionDriver.setAppiumDriver(new IOSDriver<WebElement>(
								new URL(getKeyValue(CONFIGKEY.IOS_HUB_URL, PropertyFile.MOBILE_CONFIG)),
								safariOptions));
						log("IOS driver initialized on local device !!");
					}
				}
			} else if (browser.toLowerCase().contains(Browser.ANDROID.toString().toLowerCase())) {
				chromeOptions = new ChromeOptions();
				chromeOptions.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 360);
				chromeOptions.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
				chromeOptions.setCapability(MobileCapabilityType.BROWSER_NAME, Browser.CHROME.toString());
				chromeOptions.setCapability("unicodeKeyboard", true);
				chromeOptions.setCapability("resetKeyboard", true);
				chromeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				if (CommonUtil.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_SAUCELAB, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					chromeOptions.setCapability("testobject_api_key",
							CommonUtil.getKeyValue(CONFIGKEY.TEST_OBJECT_API_KEY, PropertyFile.MOBILE_CONFIG));
					chromeOptions.setCapability("name", actionDriver.getTestCaseName());
					chromeOptions.setCapability("testobject_test_live_view_url", true);
					chromeOptions.setCapability("testobject_test_report_url", true);
					if (browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
						// chromeOptions.setCapability("device",
						// Device.getAndroidTabletNameForBrowserStack());
						chromeOptions.setCapability("tabletOnly", true);
					} else {
						// chromeOptions.setCapability("device",
						// Device.getAndroidMobileDeviceNameForBrowserStack());
						chromeOptions.setCapability("phoneOnly", true);
					}
					actionDriver.setAppiumDriver(new AndroidDriver<WebElement>(new URL(
							CommonUtil.getKeyValue(CONFIGKEY.SAUCELAB_APPIUM_HUB_URL, PropertyFile.MOBILE_CONFIG)),
							chromeOptions));
					log("Android driver initialized on sauce lab !!");
				} else if (CommonUtil
						.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_BROWSERSTACK, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					String URL = "https://"
							+ CommonUtil.getKeyValue(CONFIGKEY.BROWSERSTACK_USERNAME, PropertyFile.MOBILE_CONFIG) + ":"
							+ CommonUtil.getKeyValue(CONFIGKEY.BROWSERSTACK_AUTOMATE_KEY, PropertyFile.MOBILE_CONFIG)
							+ "@hub-cloud.browserstack.com/wd/hub";
					if (browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
						deviceName = Device.getAndroidTabletNameForBrowserStack();
						chromeOptions.setCapability("device", deviceName);
					} else {
						deviceName = Device.getAndroidMobileDeviceNameForBrowserStack();
						chromeOptions.setCapability("device", deviceName);
					}
					chromeOptions.setCapability("browserstack.debug", "true");
					chromeOptions.setCapability("realMobile", "true");
					actionDriver.setAppiumDriver(new AndroidDriver<WebElement>(new URL(URL), chromeOptions));
					log("Android driver initialized on browser stack !!");
					log("Device Name : " + deviceName);
				} else if (CommonUtil
						.getKeyValue(CONFIGKEY.RUN_MOBILE_SCRIPT_ON_CROSSBROWSERTESTING, PropertyFile.MOBILE_CONFIG)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					String URL = "http://"
							+ CommonUtil.getKeyValue(CONFIGKEY.CROSSBROWSERTESTING_USERNAME, PropertyFile.MOBILE_CONFIG)
							+ ":" + CommonUtil.getKeyValue(CONFIGKEY.CROSSBROWSERTESTING_AUTOMATE_KEY,
									PropertyFile.MOBILE_CONFIG)
							+ "@hub.crossbrowsertesting.com:80/wd/hub";
					log("Cross Browser testing HUB URL : " + URL);
					if (browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
						deviceName = Device.getAndroidTabletNameForCrossBrowserTesting();
						chromeOptions.setCapability("deviceName", deviceName);
					} else {
						deviceName = Device.getAndroidMobileDeviceNameForCrossBrowserTesting();
						chromeOptions.setCapability("deviceName", deviceName);
					}
					chromeOptions.setCapability("name", deviceName + "_" + actionDriver.getTestCaseName());
					chromeOptions.setCapability("record_video", "true");
					chromeOptions.setCapability("record_network", "false");
					chromeOptions.setCapability("max_duration", 600);
					actionDriver.setAppiumDriver(new AndroidDriver<WebElement>(new URL(URL), chromeOptions));
					log("Android driver initialized on cross browser testing cloud !!");
					log("Device Name : " + deviceName);
				} else {
					if (getKeyValue(CONFIGKEY.RUN_APPIUM_SCRIPT_IN_MOBILE_MODE_ON_DESKTOP, PropertyFile.MOBILE_CONFIG)
							.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
						if (browserName.toLowerCase().contains(DeviceType.TABLET.toString())) {
							initializeTabletModeInDesktopChromeBrowser();
						} else {
							initializeMobileModeInDesktopChromeBrowser();
						}
					} else {
						chromeOptions.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
						// chromeOptions.setCapability(MobileCapabilityType.AUTOMATION_NAME, "appium");
						// chromeOptions.setCapability(MobileCapabilityType.PLATFORM_VERSION, "5.0.2");
						chromeOptions.setCapability(MobileCapabilityType.DEVICE_NAME,
								CommonUtil.getKeyValue(CONFIGKEY.ANDROID_DEVICE_ID, PropertyFile.MOBILE_CONFIG));
						actionDriver.setAppiumDriver(new AndroidDriver<WebElement>(
								new URL(getKeyValue(CONFIGKEY.ANDROID_HUB_URL, PropertyFile.MOBILE_CONFIG)),
								chromeOptions));
						log("Android driver initialized on local device/simulator !!");
					}
				}
			} else if (browser.equalsIgnoreCase(Browser.HEAD_LESS.toString())) {
				chromeOptions = new ChromeOptions();
				chromeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, "accept");
				chromeOptions.setHeadless(true);

				if (CommonUtil.CONFIG.getProperty(CONFIGKEY.RUN_WEB_SCRIPT_ON_LOCAL)
						.equalsIgnoreCase(BOOLEAN.TRUE.toString())) {
					// setChromeDriverPath();
					WebDriverManager.chromedriver().setup();
					actionDriver.setDriver(new ChromeDriver(chromeOptions));
				} else {
					actionDriver
							.setDriver(new RemoteWebDriver(new URL(getKeyValue(CONFIGKEY.WEB_HUB_URL)), chromeOptions));
				}
				log("HEAD_LESS browser initialized !!");
			} else {
				log("Invalid browser. Check the config file");
				throw new Exception("Invalid browser. Check the config file");
			}
			sessionId = actionDriver.getSessionId().toString();
			actionDriver.setImplicitWaitOnDriver(SelTestCase.WAIT_TIMEOUT);
			if (!(browserName.toLowerCase().contains(Browser.IOS.toString().toLowerCase())
					|| browserName.toLowerCase().contains(Browser.ANDROID.toString().toLowerCase()))) {
				actionDriver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
				actionDriver.manage().timeouts().setScriptTimeout(2, TimeUnit.MINUTES);
				actionDriver.manage().window().maximize();
			}

			if (actionDriver.getCapabilities().getBrowserName().equalsIgnoreCase(Browser.CHROME.toString())) {
				if (actionDriver.getCapabilities().getPlatform().toString().equalsIgnoreCase("MAC")) {
					maximizeBrowseAccodingToScreenSize();
				}
			}

			if (browser.equalsIgnoreCase(Browser.HEAD_LESS.toString())) {
				actionDriver.manage().window().setSize(new Dimension(1920, 1080));
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception(t);
		}

	}

	public FirefoxProfile getFirefoxProfile() {
		System.out.println("fetching firefox profile");
		String downloadPath = getHomeDirectory() + "/Downloads";
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.download.dir", downloadPath);
		profile.setPreference("browser.helperApps.neverAsk.openFile",
				"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
		profile.setPreference("browser.download.manager.focusWhenStarting", false);
		profile.setPreference("browser.download.manager.useWindow", false);
		profile.setPreference("browser.download.manager.showAlertOnComplete", false);
		profile.setPreference("browser.download.manager.closeWhenDone", false);
		return profile;
	}

	public void initializeLocalDriver(String browser) throws Exception {
		if (browser.equalsIgnoreCase(Browser.CHROME.toString())) {
			// setChromeDriverPath();
			WebDriverManager.chromedriver().setup();
			chromeOptions = new ChromeOptions();
			chromeOptions.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANY);
			chromeOptions.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
			chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, "accept");
			String home = getHomeDirectory();
			String downloadFolder = home + "\\Downloads";
			System.out.println("downloadFolder : " + downloadFolder);
			Map<String, Object> prefs = new LinkedHashMap<>();
			prefs.put("download.default_directory", downloadFolder);
			prefs.put("plugins.always_open_pdf_externally", true);
			prefs.put("profile.default_content_setting_values.automatic_downloads", 1);
			chromeOptions.setExperimentalOption("prefs", prefs);
			actionDriver.setDriver(new ChromeDriver(chromeOptions));
		} else if (browser.equalsIgnoreCase(Browser.IE.toString())) {
			setIEDriverPath();
			actionDriver.setDriver(new InternetExplorerDriver());
		} else if (browser.equalsIgnoreCase(Browser.FIREFOX.toString())) {
			setFirefoxDriverPath();
			firefoxOption = new FirefoxOptions();
			firefoxOption.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
			firefoxOption.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			firefoxOption.setCapability(FirefoxDriver.PROFILE, getFirefoxProfile());
			actionDriver.setDriver(new FirefoxDriver(firefoxOption));
		} else if (browser.equalsIgnoreCase(Browser.HEAD_LESS.toString())) {
			setChromeDriverPath();
			chromeOptions = new ChromeOptions();
			chromeOptions.setHeadless(true);
			actionDriver.setDriver(new ChromeDriver(chromeOptions));
			actionDriver.manage().window().setSize(new Dimension(1920, 1080));
		} else {
			throw new Exception("Browser no set");
		}
		actionDriver.manage().window().maximize();
	}

	public void initializeMobileModeInDesktopChromeBrowser() {
		// setChromeDriverPath();
		WebDriverManager.chromedriver().setup();
		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName", "Apple iPhone 6");
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setCapability("mobileEmulation", mobileEmulation);
		actionDriver.setDriver(new ChromeDriver(chromeOptions));
		Dimension screenResolution = new Dimension(375, 667);
		actionDriver.manage().window().setSize(screenResolution);
		log("Mobile mode initialized!!");
	}

	public void initializeTabletModeInDesktopChromeBrowser() {
		// setChromeDriverPath();
		WebDriverManager.chromedriver().setup();
		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName", "Apple iPhone 6");
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setCapability("mobileEmulation", mobileEmulation);
		actionDriver.setDriver(new ChromeDriver(chromeOptions));
		Dimension screenResolution = new Dimension(1024, 1366);
		actionDriver.manage().window().setSize(screenResolution);
		log("Tablet mode initialized!!");
	}

	public static void cleanOldReports() throws Exception {
		try {
			System.out.println("Cleaning older selenium reports");
			try {
				MAIN_DIR = System.getProperty("user.dir") + "/AutomationReport/";
				System.out.println("MAIN_DIR " + MAIN_DIR);
				File dir1 = new File(MAIN_DIR);
				boolean exists = dir1.exists();
				if (!exists) {
					System.out.println("the main directory you are searching does not exist : " + exists);
					dir1.mkdir(); // creating main directory if it doesn't exist
					// new File(subDir).mkdir(); // creating time-stamped
					// sub-directory
				} else {
					System.out.println("the main directory you are searching does exist : " + exists);
					// new File(subDir).mkdir();
				}
			} catch (Throwable t) {
				t.printStackTrace();
				System.out.println("FAILS TO CREATE REPORT FOLDERS");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception(t);
		}

		deleteDir(new File(MAIN_DIR));

	}

	public static boolean deleteDir(final File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = new File(dir, children[i]).delete();
				if (!success) {
					return false;
				}
			}
		}
		return true;
	}

	public void failApiTest(Throwable t, int counter) {
		t.printStackTrace();
		test.log(LogStatus.FAIL, "Fail: " + t.getMessage());
	}

	public void skipApiTest(Throwable t, int counter) {
		t.printStackTrace();
		test.log(LogStatus.SKIP, "Fail: " + t.getMessage());
	}

	public void updateSeleniumReport(Throwable t, BaseActionDriver actionDriver, int counter, String browser) {
		t.printStackTrace();
		String url = null;
		try {
			url = actionDriver.getCurrentUrl();
		} catch (Throwable e) {
			log("Unable to fetch current url. Might be browser");
		}
		if (url != null) {
			test.log(LogStatus.FAIL, "Fail: " + t.getMessage() + " | Url captured : " + url);
		} else {
			test.log(LogStatus.FAIL, "Fail: " + t.getMessage());
		}
		addScreenShot(browser);
	}

	public void failSeleniumTest(Throwable t, BaseActionDriver actionDriver, int counter, String browser) {
		t.printStackTrace();
		String url = null;
		try {
			url = actionDriver.getCurrentUrl();
		} catch (Throwable e) {
			log("Unable to fetch current url. Might be browser");
		}
		if (url != null) {
			test.log(LogStatus.FAIL, "Fail: " + t.getMessage() + " | Url captured : " + url);
		} else {
			test.log(LogStatus.FAIL, "Fail: " + t.getMessage());
		}
		addScreenShot(browser);
	}

	public void skipSeleniumTest(Throwable t, BaseActionDriver actionDriver, int counter, String browser) {
		t.printStackTrace();
		String url = null;
		try {
			url = actionDriver.getCurrentUrl();
		} catch (Throwable e) {
			log("Unable to fetch current url. Might be browser");
		}
		if (url != null) {
			test.log(LogStatus.SKIP, "Fail: " + t.getMessage() + " | Url captured : " + url);
		} else {
			test.log(LogStatus.SKIP, "Fail: " + t.getMessage());
		}
		addScreenShot(browser);
	}
	
	@SuppressWarnings("unused")
	public void passSeleniumTest(Throwable t, BaseActionDriver actionDriver, int counter, String browser) {
		String url = null;
		try {
			url = actionDriver.getCurrentUrl();
		} catch (Throwable e) {
			log("Unable to fetch current url. Might be browser");
		}
		test.log(LogStatus.PASS, "Passed Test Script");
		addScreenShot(browser);
	}

	public void addScreenShot(String browser) {
		if (browser != null) {
			if (sessionId == null) {
				screenShotName = testCaseId + "_" + browserName + "_" + RandomUtil.getRandomNumber(12) + ".jpeg";
			} else {
				screenShotName = sessionId + ".jpeg";
			}
			actionDriver.takeScreenShot(MAIN_DIR + screenShotName);
			try {
				test.log(LogStatus.INFO, "Snapshot below: " + test.addScreenCapture(screenShotName));
			} catch (Throwable e) {
				e.printStackTrace();
				test.log(LogStatus.FAIL, "Getting error while attaching screen shot " + e.getMessage());
			}
		}
	}

	public void addScreenShot(String browser, String screenshotExtension) {
		if (browser != null) {
			if (sessionId == null) {
				screenShotName = testCaseId + "_" + browserName + "_" + RandomUtil.getRandomNumber(12)
						+ screenshotExtension;
			} else {
				screenShotName = sessionId + screenshotExtension;
			}
			actionDriver.takeScreenShot(MAIN_DIR + screenShotName);
			try {
				test.log(LogStatus.INFO, "Snapshot below: " + test.addScreenCapture(screenShotName));
			} catch (Throwable e) {
				e.printStackTrace();
				test.log(LogStatus.FAIL, "Getting error while attaching screen shot " + e.getMessage());
			}
		}
	}

	public void launchApplication() throws Exception {
		actionDriver.manage().deleteAllCookies();
		actionDriver.navigate().to(getSiteUrl(appUrl));
		if (actionDriver.getBrowserName().equalsIgnoreCase(Browser.HEAD_LESS.toString())) {
			actionDriver.manage().window().setSize(new Dimension(1920, 1080));
		}
	}

	public void maximizeBrowseAccodingToScreenSize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int screenWidth = (int) toolkit.getScreenSize().getWidth();
		int screenHeight = (int) toolkit.getScreenSize().getHeight();
		Dimension screenResolution = new Dimension(screenWidth, screenHeight);
		actionDriver.manage().window().setSize(screenResolution);
		actionDriver.manage().window().maximize();
	}

	public void launchApplication(BaseAppUrl appUrl) throws Exception {
		actionDriver.manage().deleteAllCookies();
		actionDriver.get(getSiteUrl(appUrl));
		if (actionDriver.getBrowserName().toLowerCase().contains(Browser.IOS.toString().toLowerCase())) {
			actionDriver.manage().deleteAllCookies();
		}
	}

	public String getSiteUrl(BaseAppUrl appUrl2) throws Exception {
		return CommonUtil.CONFIG.getProperty(appUrl2.toString());
	}

	public void checkInvalidCertError(String browser) {
		try {
			if (browser.equalsIgnoreCase("IE")) {
				String url = actionDriver.getCurrentUrl();
				if (url.contains("invalidcert")) {
					actionDriver.navigate().to("javascript:document.getElementById('overridelink').click()");
					log("cert issue WAS there and was handled");
				} else {
					log("cert issue WAS not there");
				}
			}
		} catch (Exception e) {

		}

	}

	/**
	 * It compares the expected text with actual(read from application). If they are
	 * not equal then it throws an error and fail the test case.
	 *
	 * @param text
	 * @param locator
	 * @throws Exception
	 */
	public void verifyText(Object text, By locator) throws Exception {
		log("Verifying text : " + text);
		actionDriver.waitForAngularRequestsToFinish();
		String actual = "";
		int i = 1;
		int waitTime = 10;
		String expected = text.toString();
		try {
			expected = expected.toString().toLowerCase().trim();
			log("Expected Text : " + expected);
			while (i <= waitTime) {
				actual = actionDriver.waitForElementToBePresent(locator).getText().toLowerCase().trim();
				log("ACTUAL TEXT IS : " + actual);
				if (actual.contains(expected) || actual.equalsIgnoreCase(expected)) {
					break;
				}
				log("Wait : " + i + " Sec");
				Thread.sleep(2000);
				i = i + 1;
			}
		} catch (StaleElementReferenceException t) {

		} catch (Throwable t) {
			throw new Exception(locator + " is missing " + t);
		}

		if (i > waitTime) {
			log("Error - >" + " Actual : " + actual + " Expected : " + expected);
			throw new Exception(" Actual : " + actual + " Expected : " + expected);
		}
	}

	public void verifyTextNotPresent(Object text, By locator) throws Exception {
		log("Verifying text not present " + text);
		String expected = text.toString().toLowerCase().trim();
		log("Following Text should not be present: " + expected);
		boolean isConditionVerified = false;
		for (int i = 0; i < 30; i++) {
			String actual = actionDriver.getElement(locator).getText().toLowerCase();
			if (!(actual.contains(expected) || actual.equalsIgnoreCase(expected))) {
				isConditionVerified = true;
				break;
			}
			System.out.println("waiting..");
			Thread.sleep(1000);
		}
		if (!isConditionVerified) {
			throw new Exception("Issue : text is present which should not be.");
		}
	}

	public void compareText(Object actual, Object expected) throws Exception {
		log("Comparing text ");
		log("Expected Text : " + expected);
		log("ACTUAL TEXT IS : " + actual);
		if (actual == null) {
			throw new Exception("Issue : Cant compare as actual text is null");
		}
		String expectedText = expected.toString().toLowerCase().trim();
		String actualText = actual.toString().toLowerCase().trim();

		if (!(actualText.contains(expectedText) || actualText.equalsIgnoreCase(expectedText))) {
			throw new Exception("Issue : Actual : " + actual + " but Expected : " + expected);
		}
	}

	public void compareTextNotEquals(String text1, String text2) throws Exception {
		log("Comparing text Not equals");
		log("Text 1 : " + text1);
		log("Text 2 : " + text2);
		if (text1 == null || text2 == null) {
			throw new Exception("Issue : Cant compare as text is null");
		}
		text1 = text1.toString().toLowerCase().trim();
		text2 = text2.toString().toLowerCase().trim();
		if (text1.contains(text2) || text1.equalsIgnoreCase(text2)) {
			throw new Exception("Issue : Both text are same");
		}
	}

	public <T extends Number> void compareNumberNotEquals(T num1, T num2, Object... msg) throws Exception {
		log("Comparing Numeric Value not equals ");
		log("Number 1 : " + num2);
		log("Number 2 IS : " + num1);
		if (num1.doubleValue() == num2.doubleValue()) {
			throw new Exception("Issue : Numbers should not be equals. Number 1 : " + num1 + " but Number 2 : " + num2
					+ ". " + msg[0].toString());
		}
	}

	public <T extends Number> void compareNumericValue(T actual, T expected) throws Exception {
		log("Comparing Numeric Value ");
		log("Expected Text : " + expected);
		log("ACTUAL TEXT IS : " + actual);
		if (!(actual.doubleValue() == expected.doubleValue())) {
			throw new Exception("Issue : Actual : " + actual + " but Expected : " + expected);
		}
	}

	public void compareNumericValue(Object actual, Object expected) throws Exception {
		log("Comparing Numeric Value ");
		log("Expected Text : " + expected);
		log("ACTUAL TEXT IS : " + actual);
		double expectedVal = Double.parseDouble(expected.toString().trim());
		double actualVal = Double.parseDouble(actual.toString().replaceAll("[$,]", "").trim());
		if (!(expectedVal == actualVal)) {
			throw new Exception("Issue : Actual : " + actual + " but Expected : " + expected);
		}
	}

	public void compareNumericValue(Object actual, Object expected, boolean continueScript) throws Exception {
		log("Comparing Numeric Value ");
		log("Expected Text : " + expected);
		log("ACTUAL TEXT IS : " + actual);
		double expectedVal = Double.parseDouble(expected.toString().trim());
		double actualVal = Double.parseDouble(actual.toString().replaceAll("[$,]", "").trim());
		if (!(expectedVal == actualVal)) {
			if (continueScript) {
				logFail("Issue : Actual : " + actual + " but Expected : " + expected);
			} else {
				throw new Exception("Issue : Actual : " + actual + " but Expected : " + expected);
			}
		}
	}

	public double add(Object... values) {
		double result = 0;
		for (Object ob : values) {
			result = result + (Double.parseDouble(ob.toString().replaceAll("[$,]", "")));
		}
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.parseDouble(df.format(result));
	}

	public void verifyText(Object text, By locator, int waitTimeInSeconds) throws Exception {
		log("Verifying text : " + text);
		String actual = "";
		int i = 1;
		String expected = text.toString();
		try {
			expected = expected.toString().toLowerCase().trim();
			log("Expected Text : " + expected);
			while (i <= waitTimeInSeconds) {
				actual = actionDriver.waitForElementToBePresent(locator).getText().toLowerCase().trim();
				log("ACTUAL TEXT IS : " + actual);
				if (actual.contains(expected) || actual.equalsIgnoreCase(expected)) {
					break;
				}
				log("Wait : " + i + " Sec");
				Thread.sleep(2000);
				i = i + 1;
			}
		} catch (StaleElementReferenceException t) {

		} catch (Throwable t) {
			throw new Exception(locator + " is missing " + t);
		}

		if (i > waitTimeInSeconds) {
			log("Error - >" + " Actual : " + actual + " Expected : " + expected);
			throw new Exception(" Actual : " + actual + " Expected : " + expected);
		}
	}

	public <T extends Number> void verifyNumbericValue(T expectedValue, By locator, int waitTimeInSeconds)
			throws Exception {
		log("Verifying numeric value : " + expectedValue);
		double actual = 0;
		double expected = Double.parseDouble(expectedValue.toString());
		int i = 1;
		try {
			log("Expected Text : " + expectedValue);
			while (i <= waitTimeInSeconds) {
				if (actionDriver.getTextWithoutSpecialCharacters(locator).trim().length() > 0) {
					actual = Double.parseDouble(actionDriver.getTextWithoutSpecialCharacters(locator).trim());
					log("ACTUAL TEXT IS : " + actual);
					if (actual == expected) {
						break;
					}
				}
				log("Waiting...");
				Thread.sleep(1000);
				i = i + 1;
			}
		} catch (StaleElementReferenceException t) {

		} catch (Throwable t) {
			throw new Exception(t);
		}

		if (i > waitTimeInSeconds) {
			log("Error - >" + " Actual : " + actual + " Expected : " + expected);
			throw new Exception(" Actual : " + actual + " Expected : " + expected);
		}
	}

	public void verifyInputBoxValue(String expected, By locator) throws Exception {
		log("Verifying text");
		String actual = "";
		int i = 1;
		int waitTime = 10;
		try {
			expected = expected.toLowerCase().trim();
			log("Expected Text : " + expected);
			while (i <= waitTime) {
				actual = actionDriver.getElement(locator).getAttribute("value").toLowerCase().trim();
				log("ACTUAL TEXT IS : " + actual);
				if (actual.contains(expected) || actual.equalsIgnoreCase(expected)) {
					break;
				}
				log("Wait : " + i + " Sec");
				Thread.sleep(2000);
				i = i + 1;
			}
		} catch (StaleElementReferenceException t) {

		} catch (Throwable t) {
			throw new Exception(locator + " is missing " + t);
		}

		if (i > waitTime) {
			log("Error - >" + " Actual : " + actual + " Expected : " + expected);
			throw new Exception(" Actual : " + actual + " Expected : " + expected);
		}
	}

	public String getDate(String dateFormat, int addDays) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, addDays); // Add days
		SimpleDateFormat ft = new SimpleDateFormat(dateFormat);
		log(ft.format(c.getTime()));
		return ft.format(c.getTime());
	}

	public static String datetimeStamp() {
		String randomString = Double.toString(Math.random());
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + "."
				+ randomString.substring(randomString.length() - 3);
	}

	public void killDriverServerIfRunning() throws Exception {
		String line;
		String pidInfo = "";
		Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null) {
			pidInfo += line;
		}
		input.close();
		if (pidInfo.contains("chromedriver.exe")) {
			Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
			log("Killing chromeDriver.exe process");
		} else {
			log("chromeDriver.exe process is not running. Starting Scripts execution on chrome");
		}
		if (pidInfo.contains("IEDriverServer.exe")) {
			Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
			log("Killing IEDriverServer.exe process");
		} else {
			log("IEDriverServer.exe process is not running. Starting Scripts execution on IE");
		}
		if (pidInfo.contains("geckodriver.exe")) {
			Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
			log("Killing geckodriver.exe process");
		} else {
			log("geckodriver.exe process is not running. Starting Scripts execution on IE");
		}
	}

	// make zip of reports
	public static void zipFolder(String filepath) {
		try {
			File inFolder = new File(filepath);
			File outFolder = new File("Reports.zip");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));
			BufferedInputStream in = null;
			byte[] data = new byte[1000];
			String files[] = inFolder.list();
			for (int i = 0; i < files.length; i++) {
				in = new BufferedInputStream(new FileInputStream(inFolder.getPath() + "/" + files[i]), 1000);
				out.putNextEntry(new ZipEntry(files[i]));
				int count;
				while ((count = in.read(data, 0, 1000)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry();
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void verifyValueNotZero(By locator) throws Exception {
		log("In verifyValueNotZero function ");
		double val;
		boolean check = true;
		for (int i = 0; i < 10; i++) {
			log("Value : " + actionDriver.getTextWithoutSpecialCharacters(locator));
			if (actionDriver.getTextWithoutSpecialCharacters(locator).trim().length() > 1) {
				val = Double.parseDouble(actionDriver.getTextWithoutSpecialCharacters(locator));
				if (val != 0) {
					check = false;
					break;
				}
			}
			Thread.sleep(2000);
		}
		if (check) {
			throw new Exception("Value should not be zero. Refer screenshot");
		}
	}

	public void amountIncreased(By locator, Object value) throws Exception {
		log("In amountIncreased function ");
		double actualVal;
		boolean check = true;
		for (int i = 0; i < 10; i++) {
			actualVal = Double.parseDouble(actionDriver.getElement(locator).getText().replaceAll("[$,]", ""));
			log("value " + actualVal);
			if (actualVal > Double.parseDouble(value.toString())) {
				check = false;
				break;
			}
			Thread.sleep(2000);
		}
		if (check) {
			throw new Exception("Value should increase by some account. Refer screenshot");
		}
	}

	public void amountDecreased(By locator, Object value) throws Exception {
		log("In amountDecreased function ");
		double actualVal;
		boolean check = true;
		for (int i = 0; i < 10; i++) {
			actualVal = Double.parseDouble(actionDriver.getTextWithoutSpecialCharacters(locator));
			log("value " + actualVal);
			if (actualVal < Double.parseDouble(value.toString())) {
				check = false;
				break;
			}
			Thread.sleep(2000);
		}
		if (check) {
			throw new Exception("Value should decrease by some account. Refer screenshot");
		}
	}

	public void verifyValueEqualsToZero(By locator) throws Exception {
		log("In verifyValueEqualToZero function ");
		log("Locator : " + locator);
		double val;
		boolean check = true;
		for (int i = 0; i < 10; i++) {
			if (actionDriver.getTextWithoutSpecialCharacters(locator) != null
					&& actionDriver.getTextWithoutSpecialCharacters(locator).length() > 0) {
				val = Double.parseDouble(actionDriver.getTextWithoutSpecialCharacters(locator));
				log("value " + val);
				if (val == 0) {
					check = false;
					break;
				}
			}

			Thread.sleep(1000);
		}
		if (check) {
			throw new Exception("Value should be zero. Refer screenshot");
		}
	}

	public void verifyValueNotEqualsTo(Object value, By locator) throws Exception {
		log("In verifyValueEqualToZero function ");
		double actualValue;
		double expectedValue = Double.parseDouble(value.toString().replaceAll("[*$,%/\\(\\)]", "").trim());
		boolean check = true;
		for (int i = 0; i < 10; i++) {
			actualValue = Double.parseDouble(actionDriver.getTextWithoutSpecialCharacters(locator));
			log("value " + actualValue);
			if (actualValue != expectedValue) {
				check = false;
				break;
			}
			Thread.sleep(1000);
		}
		if (check) {
			throw new Exception("Value should not be equals. Refer screenshot");
		}
	}

	public void verifyTextNotNull(By locator) throws Exception {
		log("In verifyTextNotNull function : ");
		boolean check = true;
		for (int i = 0; i < 10; i++) {
			log(actionDriver.getElement(locator).getText());
			if (actionDriver.getElement(locator).getText().length() > 2) {
				check = false;
				break;
			}
			log("Value is null. Trying again !!");
			Thread.sleep(2000);
		}
		if (check) {
			throw new Exception("Value should not be empty. Refer screenshot");
		}
	}

	public void verifyText(String text) throws Exception {
		log("Verifying text : " + text);
		String elementXpath = "//*[contains(text(),'" + text + "')]";
		actionDriver.waitForElementToBePresent(By.xpath(elementXpath));
	}

	public void verifyText(String text, String Class) throws Exception {
		log("Verifying text : " + text);
		String elementXpath = "//*[@class= '" + Class + "' and contains(text(), '" + text + "')]";
		// Example - //*[@class= 'displayText' and contains(text(), 'SO16768')]
		log("SO Element XPATH : " + elementXpath);
		actionDriver.waitForElementToBePresent(By.xpath(elementXpath));
	}

	public void verifyText(String text, int timeOut) throws Exception {
		log("Verifying text : " + text);
		String elementXpath = "//*[contains(text(),'" + text + "')]";
		actionDriver.waitForElementToBePresent(By.xpath(elementXpath), timeOut);
	}

	public List<String> switchToTabWindow() throws Exception {
		boolean flag = true;
		Set<String> handles = null;
		for (int i = 0; i < 7; i++) {
			handles = actionDriver.getWindowHandles();
			if (handles.size() == 2) {
				flag = false;
				break;
			}
			log("Waiting for new tab to open..");
			Thread.sleep(5000);
		}
		if (flag) {
			throw new Exception("Tab window is not opened");
		} else {
			log("Moving ahead on new tab");
		}
		Iterator<String> iterator = handles.iterator();
		List<String> windowsId = new ArrayList<String>();
		windowsId.add(iterator.next());
		windowsId.add(iterator.next());
		actionDriver.close();
		actionDriver.switchTo().window(windowsId.get(1));
		return windowsId;

	}

	public List<String> getWindowsId() {
		Set<String> handles = actionDriver.getWindowHandles();
		Iterator<String> iterator = handles.iterator();
		List<String> windowsId = new ArrayList<String>();
		while (iterator.hasNext()) {
			windowsId.add(iterator.next());
		}
		return windowsId;
	}

	public List<String> switchToTabWithoutClosingMainWindow() throws Exception {
		boolean flag = true;
		Set<String> handles = null;
		for (int i = 0; i <= 3; i++) {
			handles = actionDriver.getWindowHandles();
			if (handles.size() == 2) {
				flag = false;
				break;
			}
			log("Waiting for new tab to open..");
			Thread.sleep(3000);
		}
		if (flag) {
			throw new Exception("Tab window is not opened");
		} else {
			log("Moving ahead on new tab");
		}
		Iterator<String> iterator = handles.iterator();
		List<String> windowsId = new ArrayList<String>();
		windowsId.add(iterator.next());
		windowsId.add(iterator.next());
		actionDriver.switchTo().window(windowsId.get(1));
		return windowsId;

	}

	public void verifyIfEitherIsPresent(By locator1, By locator2, int timeOut) throws Exception {
		log("In verifyIfEitherIsPresent function ");
		boolean check = true;

		if (actionDriver.isElementVisible(locator1, timeOut) || actionDriver.isElementVisible(locator2, timeOut)) {
			log("One of the two element is present");
			check = false;
		}

		if (check) {
			throw new Exception("Neither of the 2 expected elements were present. Refer screenshot");
		}

	}

	public void testNgLogger(Object log) {
		Reporter.log(log + "---------<p>", true);
	}

	public void log(Object log) {
		Reporter.log(log + "---------<p>", true);
		if (test != null) {
			test.log(LogStatus.INFO, log.toString());
		}
	}

	public void logFail(Object log) {
		SelTestCase.getTest().log(LogStatus.FAIL, log.toString());
		addScreenShot(browserName);
	}

	public void initializeLogs(ExtentTest test) {
		this.test = test;
	}

	public String getDealId(String url) {
		log("Deal url " + url);
		String arr[] = new String[3];
		if (url.contains("quotes")) {
			arr = url.split("quotes/");
		} else {
			arr = url.split("booking/");
		}
		int count = arr.length;
		String dealId = arr[count - 1];
		log("Deal Id " + dealId);
		return dealId;
	}

	public String getFutureTime(String timeFormat, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, hour);
		SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
		return formatter.format(cal.getTime());
	}

	public String addMinutesInCurrentTime(String timeFormat, int min) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, min);
		SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
		return formatter.format(cal.getTime());
	}

	public String addMinutes(String timeFormat, String time, int min) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(timeFormat);
		Date d = df.parse(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, min);
		String newTime = df.format(cal.getTime());
		log("New Time " + newTime);
		return newTime;
	}

	public String getFutureDate(String dateFormat, int day) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(cal.getTime());
	}

	public String addMonths(String dateFormat, int monthCount) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, monthCount);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(cal.getTime());
	}

	public String addYear(String dateFormat, int yearCount) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, yearCount);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(cal.getTime());
	}

	public String addDaysInGivenDate(String dateFormat, String givenDate, int numberOfDays) {
		// Specifying date format that matches the given date
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		try {
			// Setting the date to the given date
			c.setTime(sdf.parse(givenDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Number of Days to add
		c.add(Calendar.DAY_OF_MONTH, numberOfDays);
		// Date after adding the days to the given date
		String newDate = sdf.format(c.getTime());
		// Displaying the new Date after addition of Days
		return newDate;
	}

	public String addMonthsInGivenDate(String dateFormat, String givenDate, int numberOfMonths) {
		// Specifying date format that matches the given date
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		try {
			// Setting the date to the given date
			c.setTime(sdf.parse(givenDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Number of Months to add
		c.add(Calendar.MONTH, numberOfMonths);
		// Date after adding the days to the given date
		String newDate = sdf.format(c.getTime());
		// Displaying the new Date after addition of Days

		return newDate;
	}

	public String addYearInGivenDate(String dateFormat, String givenDate, int numberOfYears) {
		System.out.println("Date before Year Addition: " + givenDate);
		// Specifying date format that matches the given date
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar c = Calendar.getInstance();
		try {
			// Setting the date to the given date
			c.setTime(sdf.parse(givenDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Number of Days to add
		c.add(Calendar.YEAR, numberOfYears);
		// Date after adding the days to the given date
		String newDate = sdf.format(c.getTime());
		// Displaying the new Date after addition of Days
		System.out.println("Date after Year Addition: " + newDate);

		return newDate;
	}

	public String changeDateFormat(String existingDateFormat, String updatedDateFormat, String date)
			throws ParseException {
		SimpleDateFormat f1 = new SimpleDateFormat(existingDateFormat);
		SimpleDateFormat f2 = new SimpleDateFormat(updatedDateFormat);
		Date d = f1.parse(date);
		String parsedDate = null;
		if (f2.format(d).contains("1970")) {
			parsedDate = f2.format(d).replace("1970", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		} else {
			parsedDate = f2.format(d);
		}
		return parsedDate;
	}

	public void compareDateTime(String dateTimeFormat, String expectedDate, String actualDate) throws ParseException {
		log("Comparing dates...");
		SimpleDateFormat df = new SimpleDateFormat(dateTimeFormat);
		Date d1 = df.parse(expectedDate.trim());
		log("Expected Date : " + df.format(d1));
		Date d2 = df.parse(actualDate.trim());
		log("Actual Date : " + df.format(d2));
		Assert.assertEquals(d2, d1, "Dates/Time are not same");
	}

	public void enterTestData(By locator, Object testData) throws Exception {
		String value;
		boolean isNonHandledIssueOccurred = false;
		boolean isValueVerified = false;
		Throwable error = null;
		for (int i = 0; i <= 10; i++) {
			try {
				testData = testData.toString().trim();
				actionDriver.getElement(locator).click();
				actionDriver.type(locator, testData.toString());
				if (actionDriver.getElement(locator).getAttribute("value") != null) {
					value = actionDriver.getElement(locator).getAttribute("value").trim();
					log("value in input box :" + value);
					Assert.assertEquals(value.toLowerCase().trim(), testData.toString().toLowerCase().trim());
					isValueVerified = true;
					break;
				}
			} catch (org.openqa.selenium.TimeoutException e) {
				isNonHandledIssueOccurred = true;
				break;
			} catch (Throwable e) {
				error = e;
				if (e.getMessage().toLowerCase().contains("Timed out".toLowerCase())
						|| e.getMessage().toLowerCase().contains("TimeoutException".toLowerCase())
						|| e.getMessage().toLowerCase().contains("ElementNotInteractableException".toLowerCase())) {
					log(e.getMessage());
					isNonHandledIssueOccurred = true;
					break;
				}
				log("Value doesn't match. Trying again!! ");
				Thread.sleep(2000);
			}
		}
		if (isNonHandledIssueOccurred) {
			throw new Exception(error);
		}
		if (!isValueVerified) {
			throw new Exception("Facing issue in typing test data ");
		}
	}

	public void enterCardExpDate(By locator, Object testData) throws Exception {
		String value;
		boolean isNonHandledIssueOccurred = false;
		boolean isValueVerified = false;
		Throwable error = null;
		for (int i = 0; i <= 10; i++) {
			try {
				testData = testData.toString().trim();
				actionDriver.sendKeys(locator, testData);
				value = actionDriver.getElement(locator).getAttribute("value").trim();
				if (actionDriver.getElement(locator).getAttribute("value") != null && value.length() == 5) {
					log("value in input box :" + value);
					isValueVerified = true;
					break;
				}
			} catch (org.openqa.selenium.TimeoutException e) {
				isNonHandledIssueOccurred = true;
				break;
			} catch (Throwable e) {
				error = e;
				if (e.getMessage().toLowerCase().contains("Timed out".toLowerCase())
						|| e.getMessage().toLowerCase().contains("TimeoutException".toLowerCase())
						|| e.getMessage().toLowerCase().contains("ElementNotInteractableException".toLowerCase())) {
					log(e.getMessage());
					isNonHandledIssueOccurred = true;
					break;
				}
				log("Value doesn't match. Trying again!! ");
				Thread.sleep(2000);
			}
		}
		if (isNonHandledIssueOccurred) {
			throw new Exception(error);
		}
		if (!isValueVerified) {
			throw new Exception("Facing issue in typing test data ");
		}
	}

	public void enterPhoneNumber(By locator, String testData) throws Exception {
		String value;
		boolean isTimeOutExceptionOccurred = false;
		for (int i = 1; i <= 10; i++) {
			try {
				try {
					((JavascriptExecutor) actionDriver).executeScript(
							"document.getElementsByID('ctl00_CH_ContentCtrl_s2_ctl06_c104_txtNQ_Response'[0].removeAttribute('emptyregexp');");
					actionDriver.findElement(locator).click();
					actionDriver.findElement(locator).clear();
				} catch (Exception e) {
					// ignore error
				}
				actionDriver.sendKeys(locator, testData);
				value = actionDriver.getElement(locator).getAttribute("value").replaceAll("[-() ]", "");
				log("Phone number in input box :" + value);
				Assert.assertTrue(value.trim().equalsIgnoreCase(testData));
				break;
			} catch (Throwable e) {
				if (e.getMessage().toLowerCase().contains("Timed out".toLowerCase())) {
					log("Error : Element Not Found " + e.getMessage());
					isTimeOutExceptionOccurred = true;
					break;
				}
				log("Phone number didn't match. Trying again ");
				Thread.sleep(2000);
			}
		}
		if (isTimeOutExceptionOccurred) {
			throw new Exception("Issue : Getting no element found exception");
		}
	}

	public void verifyPageTitle(String expectedTitle) throws Exception {
		boolean check = true;
		for (int i = 1; i <= 5; i++) {
			String title = actionDriver.getTitle();
			if (title.trim().equalsIgnoreCase(expectedTitle)) {
				check = false;
				log("Page Title verified | ");
				break;
			}
			Thread.sleep(1000);
		}
		if (check) {
			throw new Exception("Page Title is incorrect");
		}
	}

	public void verifyUrl(String textInUrl, int timeOut) throws Exception {
		log("Verify following text in URL : " + textInUrl);
		boolean check = true;
		for (int i = 1; i <= timeOut; i++) {
			String url = actionDriver.getCurrentUrl();
			log("actual URL : " + url);
			if (url.trim().contains(textInUrl)) {
				check = false;
				log("URL verified | ");
				break;
			}
			Thread.sleep(1000);
		}
		if (check) {
			throw new Exception("URL seems to be incorrect. Need to investigate");
		}
	}

	public int getLargestNumber(int numbers[]) {
		int smallest = numbers[0];
		int largest = numbers[0];

		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] > largest)
				largest = numbers[i];
			else if (numbers[i] < smallest)
				smallest = numbers[i];
		}
		log("Largest Number is : " + largest);
		return largest;
	}

	public <T extends Number> T getLargestNumber(List<T> list) {
		T smallest = list.get(0);
		T largest = list.get(0);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).doubleValue() > largest.doubleValue())
				largest = list.get(i);
			else if (list.get(i).doubleValue() < smallest.doubleValue())
				smallest = list.get(i);
		}
		log("Largest Number is : " + largest);
		return (T) largest;
	}

	public long subtractDates(String date1, String date2, String dateFormat) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		Date d1 = df.parse(date1);
		System.out.println(df.format(d1));
		Date d2 = df.parse(date2);
		System.out.println(df.format(d2));
		long diff = d2.getTime() - d1.getTime();
		log("Number of days : " + diff / (1000 * 60 * 60 * 24));
		return diff / (1000 * 60 * 60 * 24);

	}

	public String getPhoneNumberInUSFormat(String phone) {
		return String.format("(%s) %s-%s", phone.substring(0, 3), phone.substring(3, 6), phone.substring(6, 10));
	}

	public void verifyPhoneNumber(String actual, Object expected) throws Exception {
		actual = actual.replaceAll("[-() ]", "");
		compareText(actual, expected);
	}

	public String getInputBoxValueWithoutSpecialCharacters(By locator) throws Exception {
		return actionDriver.getElement(locator).getAttribute("value").trim().replaceAll("[$,%*/]", "");
	}

	public enum TimeZoneId {
		EASTERNTIME("America/New_York"), PACIFICTIME("America/Los_Angeles"), UTC("UTC");

		private String timeZone;

		public String toString() {
			return timeZone;
		}

		TimeZoneId(String timeZone) {
			this.timeZone = timeZone;
		}
	}

	public String getCurrentTime(TimeZoneId timeZoneId, String timeFormat) {
		ZoneId zoneId = ZoneId.of(timeZoneId.toString());
		LocalTime localTime = LocalTime.now(zoneId);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
		String formattedTime = localTime.format(formatter);
		return formattedTime;
	}

	// public String getCurentDate1(TimeZoneId timeZone, String dateFormat) throws
	// ParseException {
	// log("Get curent date in time zone : " + timeZone);
	// Calendar localTime = Calendar.getInstance();
	// int hour = localTime.get(Calendar.HOUR);
	// int minute = localTime.get(Calendar.MINUTE);
	// int second = localTime.get(Calendar.SECOND);
	// int year = localTime.get(Calendar.YEAR);
	// Calendar targetTime = new
	// GregorianCalendar(TimeZone.getTimeZone(timeZone.toString()));
	// targetTime.setTimeInMillis(localTime.getTimeInMillis());
	// hour = targetTime.get(Calendar.HOUR);
	// minute = targetTime.get(Calendar.MINUTE);
	// second = targetTime.get(Calendar.SECOND);
	// year = targetTime.get(Calendar.YEAR);
	// int day = targetTime.get(Calendar.DAY_OF_MONTH);
	// int month = targetTime.get(Calendar.MONTH);
	// String date = month + "/" + day + "/" + year;
	// String time = String.format("%02d:%02d:%02d", hour, minute, second);
	// String convertedtDate = date + " " + time;
	// log(convertedtDate);
	// return changeDateFormat("MM/dd/yyyy hh:mm:ss", dateFormat, convertedtDate);
	// }

	public String getCurrentDate(TimeZoneId timeZone, String dateFormat) throws Exception {
//		log("Get curent date in time zone : " + timeZone);
		final Date currentTime = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone.toString()));
		String convertedtDate = sdf.format(currentTime);
//		System.out.println("convertedDate : " + convertedtDate);
		return changeDateFormat("MM/dd/yyyy hh:mm:ss a", dateFormat, convertedtDate);
	}

	// public String getCurrentDate(TimeZoneId timeZoneId, String dateFormat) {
	// ZoneId zoneId = ZoneId.of(timeZoneId.toString());
	// LocalDate localDate = LocalDate.now(zoneId);
	// DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
	// String formattedDate = localDate.format(formatter);
	// return formattedDate;
	// }

	public String changeTimeZone(TimeZoneId timeZone, String dateTime, String dateTimeFormat) throws ParseException {
		TimeZone targetTz = TimeZone.getTimeZone(timeZone.toString());
		SimpleDateFormat format = new SimpleDateFormat(dateTimeFormat);
		Date d = format.parse(dateTime);
		format.setTimeZone(targetTz);
		String result = format.format(d);
		return result;
	}

	public String changeTimeZone_TO_UTC(TimeZoneId sourceTimeZone, String date, String dateFormat) {
		long sourceMillies = TimeUnit.MILLISECONDS
				.toMillis(TimeZone.getTimeZone(sourceTimeZone.toString()).getRawOffset());
		long targetMillies = TimeUnit.MILLISECONDS
				.toMillis(TimeZone.getTimeZone(TimeZoneId.UTC.toString()).getRawOffset());
		@SuppressWarnings("deprecation")
		long newMillies = new Date(date).getTime() + (targetMillies - sourceMillies);
		Date shiftedDate = new Date(newMillies);
		System.out.println("shifted => " + shiftedDate);
		DateFormat format = new SimpleDateFormat(dateFormat);
		System.out.println(format.format(shiftedDate));
		return format.format(shiftedDate);
	}

	public String getValueFromArray(String[] array) {
		Random random = new Random();
		String value = array[random.nextInt(array.length)];
		return value;
	}

	public void setChromeDriverPath() {
		if (System.getProperty("os.name").toLowerCase().contains("mac os")) {
			System.setProperty("webdriver.chrome.driver", DIRECTORY.CHROMEDRIVER_PATH_MAC);
		} else {
			System.setProperty("webdriver.chrome.driver", DIRECTORY.CHROMEDRIVER_PATH_WINDOWS);
		}
	}

	public void setFirefoxDriverPath() {
		if (System.getProperty("os.name").toLowerCase().contains("mac os")) {
			System.setProperty("webdriver.gecko.driver", DIRECTORY.FIREFOXDRIVER_PATH_MAC);
		} else {
			System.setProperty("webdriver.gecko.driver", DIRECTORY.FIREFOXDRIVER_PATH_WINDOWS);
		}
	}

	public void setIEDriverPath() {
		System.setProperty("webdriver.ie.driver", DIRECTORY.IEDRIVER_PATH);
	}

	public enum PropertyFile {
		CONFIG, MOBILE_CONFIG
	}

	public static String getKeyValue(String key) {
		if (!isInitialConfigurationDone) {
			try {
				setInitialConfigurations();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return CONFIG.getProperty(key);
	}

	public static String getKeyValue(String key, PropertyFile file) {
		if (file == PropertyFile.CONFIG) {
			return CONFIG.getProperty(key);
		} else if (file == PropertyFile.MOBILE_CONFIG) {
			return MOBILE_CONFIG.getProperty(key);
		} else {
			return null;
		}
	}

	public String getCurrentBrowserName() {
		log("Curent browser name : " + actionDriver.getCapabilities().getBrowserName());
		return actionDriver.getCapabilities().getBrowserName();
	}

	public String getXpath(String text) {
		return "//*[contains(text()," + "\"" + text + "\"" + ")]";
	}

	public void deleteFile(String fileName) {
		log("Deleting older files !!!");
		String directory = getHomeDirectory() + "/Downloads";
		File dir = new File(directory);

		File[] foundFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName);
			}
		});

		if (foundFiles.length > 0) {
			for (int i = 0; i < foundFiles.length; i++) {
				foundFiles[i].delete();
			}
			log("Older files deleted !!!");
		} else {
			log("Older files doesn't exists");
		}
	}

	public boolean isFileDownloaded(String fileName) {
		log("Veriying if file is downloaded or not..");
		try {
			Thread.sleep(2000);
			String home = getHomeDirectory();
			File dir = new File(home + "/Downloads");
			File[] foundFiles = null;
			long before;
			long after;
			boolean isFileDownloaded = false;
			for (int i = 0; i < 7; i++) {
				foundFiles = getFiles(dir, fileName);
				if (foundFiles.length > 0) {
					before = getFiles(dir, fileName)[0].length();
					log(before);
					Thread.sleep(2000); // wait for 2 seconds
					after = getFiles(dir, fileName)[0].length();
					log(after);
					if (before == after) {
						isFileDownloaded = true;
						break;
					}
					log("Downloading in progress");
				} else {
					Thread.sleep(10000);
					log("Waiting for downloading to start");
				}
				Thread.sleep(2000);
			}

			if (isFileDownloaded) {
				log("Download Completed !!!");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static File[] getFiles(File dir, String fileName) {
		File[] foundFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName);
			}
		});

		return foundFiles;
	}

	public String selectValueFromArray(String[] array) {
		return array[new Random().nextInt(array.length)];
	}

	public void enterBirthDay(By locator, String dob) throws Exception {
		String testData = null;
		String value = null;
		boolean isValueVerified = false;
		boolean isTimeOutExceptionOccurred = false;
		for (int i = 0; i <= 10; i++) {
			try {
				testData = dob.trim();
				actionDriver.type(locator, testData);
				value = actionDriver.getElement(locator).getAttribute("value").replace("/", "");
				log("value in input box :" + value);
				Assert.assertEquals(value.toLowerCase().trim(),
						testData.replace("/", "").toString().toLowerCase().trim());
				isValueVerified = true;
				break;
			} catch (org.openqa.selenium.TimeoutException e) {
				isTimeOutExceptionOccurred = true;
				break;
			} catch (Throwable e) {
				e.printStackTrace();
				if (e.getMessage().toLowerCase().contains("Timed out".toLowerCase())
						|| e.getMessage().toLowerCase().contains("TimeoutException".toLowerCase())) {
					log("Error : Element Not Found " + e.getMessage());
					isTimeOutExceptionOccurred = true;
					break;
				}
				log("Value doesn't match. Trying again ");
				Thread.sleep(2000);
			}
		}
		if (isTimeOutExceptionOccurred) {
			throw new Exception("Issue : Getting no element found exception");
		}
		if (!isValueVerified) {
			throw new Exception("Facing issue in typing test data ");
		}
	}

	public void enterCardNumber(By locator, String cardNumber) throws Exception {
		String testData = null;
		String value = null;
		boolean isTimeOutExceptionOccurred = false;
		for (int i = 0; i <= 10; i++) {
			try {
				testData = cardNumber.trim().replace(" ", "");
				actionDriver.sendKeys(locator, testData);
				value = actionDriver.getElement(locator).getAttribute("value").replace(" ", "");
				log("value in input box :" + value);
				if (value.length() == 16) {
					break;
				}
			} catch (org.openqa.selenium.TimeoutException e) {
				isTimeOutExceptionOccurred = true;
				break;
			} catch (Throwable e) {
				e.printStackTrace();
				if (e.getMessage().toLowerCase().contains("Timed out".toLowerCase())
						|| e.getMessage().toLowerCase().contains("TimeoutException".toLowerCase())) {
					log("Error : Element Not Found " + e.getMessage());
					isTimeOutExceptionOccurred = true;
					break;
				}
				log("Value doesn't match. Trying again ");
				Thread.sleep(2000);
			}
		}
		if (isTimeOutExceptionOccurred) {
			throw new Exception("Issue : Getting no element found exception");
		}
	}

	public static synchronized String getTestEnvironmentName() {
		if (ENVIRONMENT_UNDER_TEST == null) {
			return TEST_ENV.getProperty(CONFIGKEY.TEST_ENV);
		} else {
			return ENVIRONMENT_UNDER_TEST;
		}
	}

	public File[] getFile(String fileName) {
		log("Get file from download folder");
		String directory = getHomeDirectory() + "/Downloads";
		File dir = new File(directory);

		File[] foundFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName);
			}
		});

		if (foundFiles.length > 0) {
			return foundFiles;
		} else {
			log("Older files doesn't exists");
			return null;
		}
	}

	public double calculatePercentage(String total, String attained) {
		Double percentage;
		percentage = (Double.parseDouble(attained) * 100 / Double.parseDouble(total));
		log("The percentage is = " + percentage + " %");
		return percentage;
	}

	public Object calculateAmountAfterDiscount(double discountInPercentage, Object total) {
		Double finalAmount;
		finalAmount = (Double.parseDouble(total.toString()) * discountInPercentage) / 100;
		finalAmount = Double.parseDouble(total.toString()) - finalAmount;
		DecimalFormat df = new DecimalFormat("#.##");
		finalAmount = Double.parseDouble(df.format(finalAmount));
		log("The finalAmount is = " + finalAmount);
		return finalAmount;
	}

	public boolean isLinkBroken(URL url) throws Exception {
		String response = " ";
		// Create an instance of HTTP URL connection
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			// connect to the URL
			connection.connect();
			// get the response
			response = connection.getResponseMessage();
			log("response: " + response);
		} catch (Exception e) {
			log(e.getMessage());
		} finally {
			// Disconnect the connection
			connection.disconnect();
		}
		if (response.equalsIgnoreCase("OK")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getHomeDirectory() {
		// String dir = "C:/Users/" + System.getProperty("user.name");
		String dir = System.getProperty("user.home");
		return dir;
	}

	public String addDollarSign(String txt) {
		if (!txt.contains("$")) {
			txt = "$" + txt;
		}
		return txt;
	}

	public String removeDollarSign(String txt) {
		if (txt.contains("$")) {
			txt = txt.replace("$", "");
		}
		return txt;
	}

	public String changeToTwoDigitDecimalFormat(Object number) {
		DecimalFormat df = new DecimalFormat("#.00");
		System.out.println(df.format(Double.parseDouble(number.toString())));
		return df.format(Double.parseDouble(number.toString()));
	}

	public String readFile(String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String everything = null;
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			everything = sb.toString();
		} finally {
			br.close();
		}
		return everything;
	}

	public String encodeFileToBase64Binary(String fileName) throws Exception {
		File file = new File(fileName);
		byte[] bytes = convertFileIntoBytes(file);
		byte[] encoded = Base64.encodeBase64(bytes);
		String encodedString = new String(encoded);
		return encodedString;
	}

	public byte[] convertFileIntoBytes(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		is.close();
		return bytes;
	}

	public void analyzeLog() {
		if (!(actionDriver.getBrowserName().contains(Browser.ANDROID.toString())
				|| actionDriver.getBrowserName().contains(Browser.IOS.toString()))) {
			LogEntries logEntries = actionDriver.manage().logs().get(LogType.BROWSER);
			for (LogEntry entry : logEntries) {
				log(entry.getMessage());
			}
		}
	}

	public void waitInSeconds(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

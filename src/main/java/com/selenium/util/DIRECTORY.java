package com.selenium.util;

public class DIRECTORY {

	public static String CHROMEDRIVER_PATH_WINDOWS = System.getProperty("user.dir")
			+ "/SeleniumDrivers/WINDOWS OS/chromedriver.exe";

	public static String CHROMEDRIVER_PATH_MAC = System.getProperty("user.dir")
			+ "/SeleniumDrivers/MAC OS/chromedriver";

	public static String IEDRIVER_PATH = System.getProperty("user.dir")
			+ "/SeleniumDrivers/WINDOWS OS/IEDriverServer.exe";

	public static String FIREFOXDRIVER_PATH_WINDOWS = System.getProperty("user.dir")
			+ "/SeleniumDrivers/WINDOWS OS/geckodriver.exe";

	public static String FIREFOXDRIVER_PATH_MAC = System.getProperty("user.dir")
			+ "/SeleniumDrivers/MAC OS/geckodriver";

	public static String SELENIUM_REPORT_PATH = System.getProperty("user.dir") + "/AutomationReport/";

	public static String TEST_ENV_PROPERTY_FILE_PATH = System.getProperty("user.dir")
			+ "/src/com/selenium/setup/testEnv.properties";

	public static String TESTDATA_FOLDER_PATH = System.getProperty("user.dir") + "/src/com/selenium/testdata/";

	public static String API_REPORT_PATH = System.getProperty("user.dir") + "/ApiReport/";

}

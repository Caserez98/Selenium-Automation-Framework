package com.selenium.util;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.selenium.setup.SelTestCase;
import com.selenium.util.actiondriver.BaseActionDriver;

public class TestNgListeners implements ITestListener {

	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static ThreadLocal<CommonUtil> common = new ThreadLocal<>();
	private static ThreadLocal<BaseActionDriver> actionDriver = new ThreadLocal<>();

	@Override
	public void onFinish(ITestContext arg0) {
		if (SelTestCase.reports != null) {
			SelTestCase.reports.flush();
		}
		test.remove();
		common.remove();
		actionDriver.remove();
	}

	@Override
	public void onStart(ITestContext arg0) {
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		actionDriver.remove();
		common.remove();
		test.remove();
		SelTestCase currentClass = (SelTestCase) arg0.getInstance();
		actionDriver.set(currentClass.actionDriver);
		common.set(currentClass.common);
		test.set(currentClass.test);
		common.get().log(arg0.getMethod().getMethodName() + " Failed!");
		test.get().log(LogStatus.FAIL, arg0.getThrowable());
		String browserName = actionDriver.get().getBrowserName().trim();
		common.get().failSeleniumTest(arg0.getThrowable(), actionDriver.get(), 0, browserName);
		// common.get().analyzeLog();
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		actionDriver.remove();
		common.remove();
		test.remove();
		SelTestCase currentClass = (SelTestCase) arg0.getInstance();
		actionDriver.set(currentClass.actionDriver);
		common.set(currentClass.common);
		test.set(currentClass.test);
		common.get().log(arg0.getMethod().getMethodName() + " skipped!");
		test.get().log(LogStatus.SKIP, arg0.getThrowable());
		String browserName = actionDriver.get().getBrowserName().trim();
		common.get().skipSeleniumTest(arg0.getThrowable(), actionDriver.get(), 0, browserName);
		// common.get().analyzeLog();
	}

	@Override
	public void onTestStart(ITestResult arg0) {
		SelTestCase currentClass = (SelTestCase) arg0.getInstance();
		actionDriver.set(currentClass.actionDriver);
		common.set(currentClass.common);
		test.set(currentClass.test);
		common.get().log("Execute test case " + currentClass.testCaseName);

		// actionDriver.set(SelTestCase.getActionDriver());
		// common.set(SelTestCase.getCommon());
		// test.set(SelTestCase.getTest());
		// common.get().log("Execute test case " + currentClass.testCaseName);
	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		// common.get().log("Test method success.....");
		// test.get().log(LogStatus.PASS, "Test passed");
		SelTestCase currentClass = (SelTestCase) arg0.getInstance();
		actionDriver.set(currentClass.actionDriver);
		common.set(currentClass.common);
		test.set(currentClass.test);
		common.get().log("Execute test case " + currentClass.testCaseName);
		String browserName = actionDriver.get().getBrowserName().trim();
		common.get().passSeleniumTest(arg0.getThrowable(), actionDriver.get(), 0, browserName);
	}

}

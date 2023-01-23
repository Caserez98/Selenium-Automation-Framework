package com.selenium.util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

public class RetryAnalyzer implements IRetryAnalyzer {
	private final ThreadLocal<Integer> retryCount = new ThreadLocal<Integer>() {
		@Override
		public Integer initialValue() {
			return Integer.valueOf(0);
		}
	};

	private ThreadLocal<Integer> retryMaxCount = new ThreadLocal<>();
	private ThreadLocal<String> testName = new ThreadLocal<>();

	@Override
	public synchronized boolean retry(ITestResult result) {
		testName.set(result.getName());
		retryMaxCount.set(1);
		if (!result.isSuccess()) {
			if (retryCount.get() < retryMaxCount.get()) {
				Reporter.log(
						"running retry logic for  '" + result.getName() + "' on class " + this.getClass().getName(),
						true);
				retryCount.set(retryCount.get().intValue() + 1);
				result.setStatus(ITestResult.FAILURE);
				return true;
			} else {
				result.setStatus(ITestResult.FAILURE);
			}
		} else {
			result.setStatus(ITestResult.SUCCESS);
		}
		return false;
	}
}

// public int retryCount = 0;
// public int retryMaxCount = 1; // set your count to re-run test
// public String testName; // changed from static to non static. Will undo if it
// impacts anything
//
// @Override
// public synchronized boolean retry(ITestResult result) {
// testName = result.getName();
// if (!result.isSuccess()) {
// if (retryCount < retryMaxCount) {
// Reporter.log(
// "running retry logic for '" + result.getName() + "' on class " +
// this.getClass().getName(),
// true);
// retryCount++;
// result.setStatus(ITestResult.FAILURE);
// return true;
// } else {
// result.setStatus(ITestResult.FAILURE);
// }
// } else {
// result.setStatus(ITestResult.SUCCESS);
// }
// return false;
// }

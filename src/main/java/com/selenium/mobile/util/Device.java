package com.selenium.mobile.util;

import java.util.Random;

import com.selenium.util.CONFIGKEY;
import com.selenium.util.CommonUtil;
import com.selenium.util.CommonUtil.PropertyFile;

public class Device {

	// ************BROWSER STACK***************************
	public static String getAndroidMobileDeviceNameForBrowserStack() {
		return getDeviceName(CONFIGKEY.BROWSERSTACK_ANDROID_MOBILE_DEVICES);
	}

	public static String getAndroidTabletNameForBrowserStack() {
		return getDeviceName(CONFIGKEY.BROWSERSTACK_ANDROID_TABLET_DEVICES);

	}

	public static String getIphoneNameForBrowserStack() {
		return getDeviceName(CONFIGKEY.BROWSERSTACK_IOS_MOBILE_DEVICES);

	}

	public static String getIpadNameForBrowserStack() {
		return getDeviceName(CONFIGKEY.BROWSERSTACK_IOS_TABLET_DEVICES);
	}

	// **************CROSS BROWSER TESTING***************************
	public static String getAndroidMobileDeviceNameForCrossBrowserTesting() {
		return getDeviceName(CONFIGKEY.CROSSBROWSERTESTING_ANDROID_MOBILE_DEVICES);
	}

	public static String getAndroidTabletNameForCrossBrowserTesting() {
		return getDeviceName(CONFIGKEY.CROSSBROWSERTESTING_ANDROID_TABLET_DEVICES);
	}

	public static String getIphoneNameForCrossBrowserTesting() {
		return getDeviceName(CONFIGKEY.CROSSBROWSERTESTING_IOS_MOBILE_DEVICES);
	}

	public static String getIpadNameForCrossBrowserTesting() {
		return getDeviceName(CONFIGKEY.CROSSBROWSERTESTING_IOS_TABLET_DEVICES);
	}

	public static int getSafariVersionForCrossBrowserTesting() {
		int version[] = { 8, 9 };
		Random random = new Random();
		int result = version[random.nextInt(version.length)];
		return result;
	}

	public static String getDeviceName(String key) {
		String devices = CommonUtil.getKeyValue(key, PropertyFile.MOBILE_CONFIG);
		String deviceArray[] = null;
		if (devices.contains(",")) {
			deviceArray = devices.split(",");
			Random random = new Random();
			return deviceArray[random.nextInt(deviceArray.length)].trim();
		} else {
			return devices.trim();
		}
	}

}

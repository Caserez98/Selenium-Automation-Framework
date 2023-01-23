package com.selenium.mobile.util;

public enum DeviceType {
	TABLET("tablet"), MOBILE("mobile");

	String deviceType;

	DeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String toString() {
		return deviceType;
	}
}

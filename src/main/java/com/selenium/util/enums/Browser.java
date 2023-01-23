package com.selenium.util.enums;

public enum Browser {

	FIREFOX("Firefox"), CHROME("Chrome"), IE("IE"), IOS("ios"), ANDROID("android"), SAFARI("Safari"),
	INTERNET_EXPLORER("Internet Explorer"), HEAD_LESS("HeadLess");

	String browserName;

	Browser(String browserName) {
		this.browserName = browserName;
	}

	public String toString() {
		return browserName;
	}
}

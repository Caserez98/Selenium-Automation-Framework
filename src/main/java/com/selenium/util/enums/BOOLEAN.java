package com.selenium.util.enums;

public enum BOOLEAN {

	TRUE("true"), FALSE("false");

	String booleanType;

	BOOLEAN(String booleanType) {
		this.booleanType = booleanType;
	}

	public String toString() {
		return booleanType;
	}
}

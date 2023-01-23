package com.selenium.testdata;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.selenium.util.CommonUtil;
import com.selenium.util.CommonUtil.TimeZoneId;
import com.selenium.util.namegenerator.NameGenerator;

//Auto-generated Javadoc
/**
 * The Class RandomUtilities.
 */
public final class RandomUtil {

	/**
	 * MY_DAYS constant is being used in wait methods.
	 */
	public static final int MY_DAYS = 365;

	/**
	 * MY_DAYS constant is being used in wait methods.
	 */
	public static final int MY_INT = 730;
	/** The words. */
	private static String[] words = new String[] { "abcd", //$NON-NLS-1$
			"posst", //$NON-NLS-1$
			"wnko", //$NON-NLS-1$
			"fsnh", //$NON-NLS-1$
			"asd", //$NON-NLS-1$
			"cvag", //$NON-NLS-1$
			"bgty", //$NON-NLS-1$
			"ghys", //$NON-NLS-1$
			"med", //$NON-NLS-1$
			"reg", //$NON-NLS-1$
			"ght", //$NON-NLS-1$
			"rtgh", //$NON-NLS-1$
			"lyop", //$NON-NLS-1$
			"inn", //$NON-NLS-1$
			"utr", //$NON-NLS-1$
			"lab", //$NON-NLS-1$
			"etp", //$NON-NLS-1$
			"doel", //$NON-NLS-1$
			"mag", //$NON-NLS-1$
			"aiuam", //$NON-NLS-1$
			"wedut", //$NON-NLS-1$
			"seeut", //$NON-NLS-1$
			"dkl", //$NON-NLS-1$
			"vpqt", //$NON-NLS-1$
			"at", //$NON-NLS-1$
			"vier", //$NON-NLS-1$
			"eisd", //$NON-NLS-1$
			"etcp", //$NON-NLS-1$
			"accu", //$NON-NLS-1$
			"etsp", //$NON-NLS-1$
			"juss", //$NON-NLS-1$
			"duspia", //$NON-NLS-1$
			"dosli", //$NON-NLS-1$
			"etpa", //$NON-NLS-1$
			"ears", //$NON-NLS-1$
			"rebm", //$NON-NLS-1$
			"sxtsa", //$NON-NLS-1$
			"clxt", //$NON-NLS-1$
			"kpa", //$NON-NLS-1$
			"guu", //$NON-NLS-1$
			"nogxi", //$NON-NLS-1$
			"saea", //$NON-NLS-1$
			"tma", //$NON-NLS-1$
			"snxcct", //$NON-NLS-1$
			"esg" }; //$NON-NLS-1$

	/**
	 * number.
	 *
	 */
	private static String[] number = { "12", "34", "25", "78", "90", "10", "31", "43", "52", "87", "09", "56", "16",
			"26", "76", "31", "35", "37", "54", "56", "58", "69", "94", "23", "45", "47", "49", "13", "15", "17", "19",
			"89", "86", "82", "51", "57", "59", "29", "24", "42", "48", "53", "43", "33", "34", "39", "22", "27", "87",
			"94", "91", "63", "65", "60", "40", "94", "81", "11", "73", "71" };

	/**
	 * random number producer.
	 */
	private static Random random = new Random();

	/**
	 * utility class, don't instantiate.
	 */
	private RandomUtil() {
		super();
	}

	/**
	 * returns a random word.
	 *
	 * @return random word
	 */
	public synchronized static String getRandomWord() {
		return words[random.nextInt(words.length)];
	}

	/**
	 * returns a random email.
	 *
	 * @return random email
	 */
	public synchronized static String getRandomEmail() {
		return getRandomWord() + getRandomNumber(7) + "auto@" + "yopmail" + ".com"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * getRandomNumber.
	 *
	 * @return String
	 */
	public synchronized static String getRandomNumber() {
		return number[random.nextInt(number.length)];
	}

	public synchronized static String getUSAreaCode() {
		String arr[] = { "818", "714" };
		return arr[random.nextInt(arr.length)];
	}

	/**
	 * getRandomPhone.
	 *
	 * @return String
	 */
	public synchronized static String getRandomPhone() {
		return "11" + RandomUtil.getRandomNumber(8);
	}

	public synchronized static String getStaticPhone() {
		return "5005550006";
	}

	public synchronized static String getRandomWebSite() {
		return "www." + getRandomWord() + getRandomWord() + getRandomWord() + ".com"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * returns a random date.
	 *
	 * @return random date
	 */
	public synchronized static Date getRandomDate() {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, MY_DAYS - random.nextInt(MY_INT));
		return calendar.getTime();
	}

	/**
	 * getRandomName.
	 *
	 * @return String
	 */
	public synchronized static String getRandomName() {
		// return getRandomWord() + getRandomWord() + getRandomWord();
		try {
			return new NameGenerator().compose(5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized static long getRandomNumber(final int digCount) {
		return getRandomNumber(digCount, new Random());
	}

	private synchronized static Long getRandomNumber(final int digCount, Random rnd) {
		final char[] ch = new char[digCount];
		for (int i = 0; i < digCount; i++) {
			ch[i] = (char) ('0' + (i == 0 ? rnd.nextInt(9) + 1 : rnd.nextInt(10)));
		}
		return Long.parseLong(new String(ch));
	}

	public static long getRandomNumber(long Min, long Max) {
		return (long) (Math.random() * (Max - Min)) + Min;
	}

	public static String getBirthday(UserType userType) {
		String month = String.valueOf(getRandomNumber(1, 12));
		if (month.length() == 1) {
			month = String.format("%02d", Integer.parseInt(month));
		}

		String day = String.valueOf(getRandomNumber(1, 28));
		if (day.length() == 1) {
			day = String.format("%02d", Integer.parseInt(day));
		}

		if (userType == UserType.MINOR) {
			return month + day + String.valueOf(getRandomNumber(2003, 2019));
		} else if (userType == UserType.PRIMARY) {
			return month + day + String.valueOf(getRandomNumber(1945, 1999));
		} else {
			return month + day + String.valueOf(getRandomNumber(1945, 1999));
		}
	}

	public enum Age {
		FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), FIFTEEN(15), SIXTEEN(16), SEVENTEEN(17), EIGHTEEN(18),
		NINETEEN(19);

		private int ageInNumbers;

		public int getAgeInNumber() {
			return ageInNumbers;
		}

		Age(int ageInNumbers) {
			this.ageInNumbers = ageInNumbers;
		}
	}

	public enum UserType {
		MINOR, PRIMARY, DEPENDENT
	}

	public static String getBirthday(Age age) throws Exception {
		CommonUtil common = new CommonUtil();
		String currentYear = common.getCurrentDate(TimeZoneId.UTC, "yyyy");
		String currentMonth = common.getCurrentDate(TimeZoneId.UTC, "MM");
		String currentDay = common.getCurrentDate(TimeZoneId.UTC, "dd");

		int birthYear = Integer.parseInt(currentYear) - age.getAgeInNumber();

		String birthDate = String.valueOf((int) RandomUtil.getRandomNumber(1, Integer.parseInt(currentDay)));
		if (birthDate.length() == 1) {
			birthDate = "0" + birthDate;
		}

		String birthDay = currentMonth + "/" + birthDate + "/" + birthYear;
		System.out.println("birthDay : " + birthDay);

		return birthDay;
	}

	public static String getBirthdayWithSlashes(UserType userType) {
		String month = String.valueOf(getRandomNumber(1, 12));
		if (month.length() == 1) {
			month = String.format("%02d", Integer.parseInt(month));
		}

		String day = String.valueOf(getRandomNumber(1, 28));
		if (day.length() == 1) {
			day = String.format("%02d", Integer.parseInt(day));
		}
		if (userType == UserType.MINOR) {
			return month + "/" + day + "/" + String.valueOf(getRandomNumber(2001, 2019));
		} else if (userType == UserType.PRIMARY) {
			return month + "/" + day + "/" + String.valueOf(getRandomNumber(1945, 1999));
		} else if (userType == UserType.DEPENDENT) {
			return month + "/" + day + "/" + String.valueOf(getRandomNumber(1945, 1999));
		}
		return null;
	}

	public static String getSocialSecurityNumber() {
		String ssn = String.valueOf(getRandomNumber(10000, 99999)) + "1234";
		return ssn;
	}

	public static String createPassword(int len) {
		System.out.println("Generating password using random() : ");
		System.out.println("Your new password is : ");

		// A strong password has Cap_chars, Lower_chars,
		// numeric value and symbols. So we are using all of
		// them to generate our password
		String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Small_chars = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String symbols = "!@#$%";

		String values = Capital_chars + Small_chars + numbers + symbols;

		// Using random method
		Random rndm_method = new Random();

		char[] password = new char[len];

		for (int i = 0; i < len - 4; i++) {
			// Use of charAt() method : to get character value
			// Use of nextInt() as it is scanning the value as int
			password[i] = values.charAt(rndm_method.nextInt(values.length() - 1));

		}
		// String pass = String.valueOf(password).trim() +
		// numbers.charAt(rndm_method.nextInt(numbers.length() - 1))
		// + symbols.charAt(rndm_method.nextInt(symbols.length() - 1))
		// + Capital_chars.charAt(rndm_method.nextInt(Capital_chars.length() - 1))
		// + Small_chars.charAt(rndm_method.nextInt(Small_chars.length() - 1));

		// return pass;
		return "Test123!";
	}

}

package com.selenium.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * GSON utilities
 *
 */
public class GsonUtility {

	private GsonUtility() {
		throw new IllegalStateException("Utility class");
	}

	public static synchronized <T> T getObjectFromJsonResource(String resourceFileName, Class<T> clazz)
			throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileData = null;
		try {

			fileData = IOUtils.toString(GsonUtility.class.getResourceAsStream(resourceFileName),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IOException("IO Exception getting report data", e);
		}
		if (fileData == null) {
			throw new NullPointerException("data not available. check file: " + resourceFileName
					+ " in resources for availability and valid data");
		}

		return gson.fromJson(fileData, clazz);
	}
}

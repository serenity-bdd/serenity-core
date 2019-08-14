package com.serenity.screenplay.platform.android.adb.commands;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serenity.screenplay.platform.android.adb.ProcessExecutor;
/**
 * 
 * e.g: Press.key(KEYCODE_DEL)
 * 
 * @author jacob
 *
 */
public class Press {

	private static final Logger LOGGER = LoggerFactory.getLogger(Press.class);

	private static final String KEYEVENT_COMMAND = "input keyevent %s";
	private static final String INPUT_TEXT_COMMAND = "input text '%s'";

	public static void key(String keyCode) {
		try {
			ProcessExecutor.exec(String.format(KEYEVENT_COMMAND, keyCode));
		} catch (IOException e) {
			LOGGER.warn("" + e.getMessage());
		}
		LOGGER.info("Key Code " + keyCode + " is pressed");
	}

	public static void type(String value) {
		try {
			ProcessExecutor.exec(String.format(INPUT_TEXT_COMMAND, value));
		} catch (IOException e) {
			LOGGER.warn("" + e.getMessage());
		}
		LOGGER.info("Type " + value);
	}

}

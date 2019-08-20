package com.serenity.screenplay.platform.android.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jacob
 *
 */
public class ProcessExecutor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessExecutor.class);

	public static void execOnDevice(String deviceId, String command) throws IOException {
		
		Process process = null;
		String commandString;
		if (deviceId != null) {
			commandString = String.format("%s", "adb -s " + deviceId + " shell " + command);
		} else
			commandString = String.format("%s", "adb shell " + command);

		System.out.print("Command is " + commandString + "\n");
		try {
			process = ProcessHelper.runTimeExec(commandString);
		} catch (IOException e) {
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.print(line + "\n");
		}
	}
	
	public static void exec(String command) throws IOException {

		Process process = null;
		String commandString;

		commandString = String.format("%s", "adb shell " + command);

		LOGGER.info("Command is " + commandString + "\n");
		try {
			process = ProcessHelper.runTimeExec(commandString);
		} catch (IOException e) {
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			LOGGER.info(line + "\n");
		}
	}

}

package net.thucydides.core.util;

import java.io.File;
import java.util.regex.Matcher;

public class FileSeparatorUtil {

	public static final String UNIX_FILE_SEPARATOR = "/";
	public static final String WINDOWS_FILE_SEPARATOR = "\\";
	
	public static String changeSeparatorIfRequired(String path) {
		
		String changedPath = path;
		
		if (File.separator.equals(WINDOWS_FILE_SEPARATOR)) {
			changedPath = path.replaceAll(UNIX_FILE_SEPARATOR, Matcher.quoteReplacement(WINDOWS_FILE_SEPARATOR));
		}
		
		return changedPath;
		
	}
	
}

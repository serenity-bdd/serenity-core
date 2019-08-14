package com.serenity.screenplay.platform.android.adb;

import java.io.IOException;
/**
 * 
 * @author jacob
 *
 */
public class ProcessHelper {
	
	private ProcessHelper() {
    }

    public static Process runTimeExec(String runTimeExec) throws IOException {
        return Runtime.getRuntime().exec(runTimeExec);
    }

}

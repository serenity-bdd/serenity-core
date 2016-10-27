package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.os.CommandLine;

import java.io.File;

import static com.google.common.base.Preconditions.checkState;

public class DriverServiceExecutable {


    protected static File findExecutable(String exeName,
                                         String exeProperty,
                                         String exeDocs,
                                         String exeDownload) {
        String defaultPath = CommandLine.find(exeName);
        String exePath = System.getProperty(exeProperty, defaultPath);
        checkState(exePath != null,
                "The path to the driver executable must be set by the %s system property;"
                        + " for more information, see %s. "
                        + "The latest version can be downloaded from %s",
                exeProperty, exeDocs, exeDownload);

        File exe = new File(exePath);
        checkExecutable(exe);
        return exe;
    }

    protected static void checkExecutable(File exe) {
        checkState(exe.exists(),
                "The driver executable does not exist: %s", exe.getAbsolutePath());
        checkState(!exe.isDirectory(),
                "The driver executable is a directory: %s", exe.getAbsolutePath());
        checkState(FileHandler.canExecute(exe),
                "The driver is not executable: %s", exe.getAbsolutePath());
    }
}

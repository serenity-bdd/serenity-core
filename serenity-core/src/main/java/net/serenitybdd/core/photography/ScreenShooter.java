package net.serenitybdd.core.photography;

import java.io.IOException;

public interface ScreenShooter {
    byte[] takeScreenshot() throws IOException;
}

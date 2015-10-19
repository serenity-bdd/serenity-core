package net.serenitybdd.core.photography;

import java.nio.file.Path;

public interface PhotoFilter {

    Path amendedScreenshotPath(ScreenshotNegative negative);
    ScreenshotNegative process(ScreenshotNegative negative);

}

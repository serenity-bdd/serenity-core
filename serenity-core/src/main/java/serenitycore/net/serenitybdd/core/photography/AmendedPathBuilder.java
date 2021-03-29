package serenitycore.net.serenitybdd.core.photography;

import java.nio.file.Path;

public class AmendedPathBuilder {
    private final ScreenshotNegative negative;

    public AmendedPathBuilder(ScreenshotNegative negative) {
        this.negative = negative;
    }

    public Path withPrefix(String prefix) {
        return negative.getScreenshotPath().getParent().resolve(prefix + negative.getScreenshotPath().getFileName().toString());
    }
}

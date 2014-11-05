package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ScreenshotDigest {

    public ScreenshotDigest(EnvironmentVariables environmentVariables, BlurLevel blurLevel) {
        this.environmentVariables = environmentVariables;
        this.blurLevel = Optional.fromNullable(blurLevel);
    }

    private final Optional<BlurLevel> blurLevel;
    private final EnvironmentVariables environmentVariables;

    public String forScreenshot(File screenshotFile) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(screenshotFile))
               + "_" + blurLevel.or(BlurLevel.NONE).toString()
               + optionalWidth()
               + ".png";
    }

    private String optionalWidth() {
        return environmentVariables.getProperty(ThucydidesSystemProperty.THUCYDIDES_RESIZED_IMAGE_WIDTH, "");
    }
}

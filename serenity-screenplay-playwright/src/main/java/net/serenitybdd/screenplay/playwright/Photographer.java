package net.serenitybdd.screenplay.playwright;

import com.microsoft.playwright.Page;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Photographer {
    public File takesAScreenshot(Page currentPage) {
        BaseStepListener baseStepListener = StepEventBus.getEventBus().getBaseStepListener();

        byte[] screenshot = currentPage.screenshot(new Page.ScreenshotOptions().setFullPage(true));

        try {
            Path outputDirectory = baseStepListener.getOutputDirectory().toPath();
            Path screenshotFile = Files.createTempFile(outputDirectory, "screenshot", ".png");
            Files.write(screenshotFile, screenshot);

            return screenshotFile.toFile();
        } catch (IOException e) {
            Assertions.fail("Failed to take Playwright screenshot", e);
            return null;
        }
    }
}

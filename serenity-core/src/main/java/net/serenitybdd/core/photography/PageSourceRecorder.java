package net.serenitybdd.core.photography;


import net.thucydides.core.webdriver.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class PageSourceRecorder {
    private final WebDriver driver;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PageSourceRecorder(WebDriver driver) {
        this.driver = driver;
    }

    public Optional<File> intoDirectory(Path path) {
        byte[] pageSource = getPageSource();
        if (WebDriverFactory.isAlive(driver) && (pageSource.length > 0)) {
            try {
                Path pageSourceFile = Files.createTempFile(path, "pagesource", ".html.txt");
                Files.write(pageSourceFile, pageSource);
                return Optional.of(pageSourceFile.toFile());
            } catch (IOException couldNotCreatePageSourcce) {
                LOGGER.warn("Could not save the page source HTML file", couldNotCreatePageSourcce);
            }
        }
        return Optional.empty();
    }

    private byte[] getPageSource() {
        try {
            String ps = driver.getPageSource();
            if (ps == null) { return new byte[]{}; }
            return ps.getBytes(StandardCharsets.UTF_8);
        } catch(Exception e) {
            LOGGER.warn("Failed to get page source", e);
            return new byte[]{};
        }
    }
}

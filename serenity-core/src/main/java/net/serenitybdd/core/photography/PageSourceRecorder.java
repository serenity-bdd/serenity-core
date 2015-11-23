package net.serenitybdd.core.photography;


import com.google.common.base.Optional;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PageSourceRecorder {
    private final WebDriver driver;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public PageSourceRecorder(WebDriver driver) {
        this.driver = driver;
    }

    public Optional<File> intoDirectory(Path path) {
        try {
            Path pageSourceFile = Files.createTempFile(path, "pagesource", ".html.txt");
            Files.write(pageSourceFile, pageSource());
            return Optional.of(pageSourceFile.toFile());
        } catch(IOException couldNotCreatePageSourcce) {
            LOGGER.warn("Could not save the page source HTML file", couldNotCreatePageSourcce);
        }
        return Optional.absent();
    }

    private byte[] pageSource() {
        if (driver.getPageSource() == null) {  return new byte[]{}; }
        return driver.getPageSource().getBytes();
    }
}

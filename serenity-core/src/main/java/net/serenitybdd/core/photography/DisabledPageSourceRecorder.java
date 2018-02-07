package net.serenitybdd.core.photography;

import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class DisabledPageSourceRecorder extends PageSourceRecorder {
    public DisabledPageSourceRecorder(WebDriver driver) {
        super(driver);
    }

    @Override
    public Optional<File> intoDirectory(Path path) {
        return Optional.empty();
    }
}

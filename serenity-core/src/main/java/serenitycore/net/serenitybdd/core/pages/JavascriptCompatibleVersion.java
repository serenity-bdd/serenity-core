package serenitycore.net.serenitybdd.core.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

class JavascriptCompatibleVersion {
    public static Optional<JavascriptExecutor> of(WebDriver driver) {
        if (driver instanceof JavascriptExecutor) {
            return Optional.of((JavascriptExecutor) driver);
        }
        return Optional.empty();
    }
}
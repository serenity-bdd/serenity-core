package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * {@link Switch#toWindow(String)} is not always correct! The method signature
 * seems to suggest you will be switching to "windowTitle". However you are
 * actually switching to "window with the given name/handle"; see
 * {@link TargetLocator#window(String)}. The window name and window title are
 * <strong>not</strong> always the same; see <a href=
 * "https://developer.mozilla.org/en-US/docs/Web/API/Window/name">documentation</a>.
 */
public class SwitchToWindowTitle implements Task {

    private static final Logger log = LoggerFactory.getLogger(SwitchToWindowTitle.class);

    private final String title;

    /**
     * Call this from {@link Switch#toWindowTitled(String)}.
     */
    SwitchToWindowTitle(String title) {
        this.title = title;
    }

    @Override
    @Step("{0} switches to window titled '#title'")
    public <T extends Actor> void performAs(T actor) {

        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        Set<String> allHandles = driver.getWindowHandles();
        log.debug("open windows: " + allHandles.size());
        if (allHandles.size() < 2) {
            log.error("Not enough windows open!");
            return;
        }

        String firstHandle = driver.getWindowHandle();
        Dimension windowSize = driver.manage().window().getSize();

        log.debug("current window: " + driver.getTitle() + ", at: " + driver.getCurrentUrl());
        boolean found = false;
        for (String handle : allHandles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().contentEquals(title)) {
                found = true;
                break;
            }
        }
        if (!found) {
            log.error("Could not find window with title: '" + title + "'");
            driver.switchTo().window(firstHandle);
        }

        // new window is different size when headless
        if (!driver.manage().window().getSize().equals(windowSize)) {
            log.warn("Window size does not match!");
            driver.manage().window().setSize(windowSize);
        }
    }
}

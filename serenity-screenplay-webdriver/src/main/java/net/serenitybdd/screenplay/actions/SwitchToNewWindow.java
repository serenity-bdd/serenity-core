package net.serenitybdd.screenplay.actions;

import java.util.Set;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Step;

/**
 * If you just opened a (second?) new window and want to switch to it.
 */
public class SwitchToNewWindow implements Task {

    private static final Logger log = LoggerFactory.getLogger(SwitchToNewWindow.class);

    /**
     * Call this from {@link Switch#toNewWindow()}.
     */
    SwitchToNewWindow() { }

    @Override
    @Step("{0} switches to new window")
    public <T extends Actor> void performAs(T actor) {

	WebDriver driver = BrowseTheWeb.as(actor).getDriver();

	Set<String> allHandles = driver.getWindowHandles();
	if (allHandles.size() < 2) {
	    log.error("Not enough windows open!");
	    return;
	}

	String firstHandle = driver.getWindowHandle();
	Dimension windowSize = driver.manage().window().getSize();

	for (String handle : allHandles) {
	    if (!handle.equals(firstHandle)) {
		driver.switchTo().window(handle);
		break;
	    }
	}

	// new window is different size when headless
	if (!driver.manage().window().getSize().equals(windowSize)) {
	    log.warn("Window size does not match!");
	    driver.manage().window().setSize(windowSize);
	}
    }
}

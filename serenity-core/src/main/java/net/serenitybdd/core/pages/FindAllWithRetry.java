package net.serenitybdd.core.pages;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.function.Function;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_RETRY_ON_STALE_ELEMENT_EXCEPTION;

public class FindAllWithRetry {

    private final EnvironmentVariables environmentVariables;

    public FindAllWithRetry(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public ListOfWebElementFacades find(Function<PageObject, ListOfWebElementFacades> finder, PageObject page) {
        ListOfWebElementFacades matchingElements = finder.apply(page);
        if (shouldRetryOnStaleElementException()) {
            matchingElements.setFallback(finder, page);
        }
        return matchingElements;
    }

    private boolean shouldRetryOnStaleElementException() {
        return WEBDRIVER_RETRY_ON_STALE_ELEMENT_EXCEPTION.booleanFrom(environmentVariables, true);
    }
}

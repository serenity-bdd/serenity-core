package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.selectors.Selectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.exceptions.ElementNotFoundAfterTimeoutError;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class MultiXPathOrCssTarget extends SearchableTarget {

    private final String[] cssOrXPathSelectors;

    public MultiXPathOrCssTarget(String targetElementName, Optional<IFrame> iFrame, Optional<Duration> timeout, String... cssOrXPathSelectors) {
        super(targetElementName, iFrame, timeout);
        this.cssOrXPathSelectors = cssOrXPathSelectors;
    }

    public WebElementFacade resolveFor(PageObject page) {
        Duration effectiveTimeout = timeout.orElse(page.getImplicitWaitTimeout());

        Optional<WebElementFacade> resolvedElement = findFirstMatching(page, cssOrXPathSelectors);
        if (resolvedElement.isPresent()) {
            return resolvedElement.get();
        } else {
            long maxTimeAllowed = effectiveTimeout.toMillis();
            long timeStarted = System.currentTimeMillis();
            while (System.currentTimeMillis() - timeStarted < maxTimeAllowed) {
                resolvedElement = findFirstMatching(page, cssOrXPathSelectors);
                if (resolvedElement.isPresent()) {
                    return resolvedElement.get();
                }
            }
        }
        StepEventBus.getEventBus().notifyFailure();
        throw new ElementNotFoundAfterTimeoutError("No element was found after " +
                (effectiveTimeout.toMillis() / 1000) + "s for " + getName() + System.lineSeparator()
                +"We tried with the following locators: " + Arrays.toString(cssOrXPathSelectors));
    }

    private Optional<WebElementFacade> findFirstMatching(PageObject page, String... cssOrXPathSelectors) {

        for(String selector : cssOrXPathSelectors) {
            List<WebElementFacade> matchingElements = page.withTimeoutOf(Duration.ZERO).findAll(selector);
            if (!matchingElements.isEmpty()) {
                return Optional.of(matchingElements.get(0));
            }
        }
        return Optional.empty();
    }

    private List<WebElementFacade> findAllMatching(PageObject page, String... cssOrXPathSelectors) {
        for(String selector : cssOrXPathSelectors) {
            List<WebElementFacade> matchingElements = page.withTimeoutOf(Duration.ZERO).findAll(selector);
            if (!matchingElements.isEmpty()) {
                return matchingElements;
            }
        }
        return new ArrayList<>();
    }

    public List<WebElementFacade> resolveAllFor(PageObject page) {
        List<WebElementFacade> resolvedElements =  findAllMatching(page, cssOrXPathSelectors);

        if (timeout.isPresent()) {
            if (resolvedElements.isEmpty()) {
                return resolvedElements;
            } else {
                Duration effectiveTimeout = timeout.orElse(page.getImplicitWaitTimeout());
                long maxTimeAllowed = effectiveTimeout.toMillis();
                long timeStarted = System.currentTimeMillis();
                while (System.currentTimeMillis() - timeStarted < maxTimeAllowed) {
                    resolvedElements = findAllMatching(page, cssOrXPathSelectors);
                    if (!resolvedElements.isEmpty()) {
                        return resolvedElements;
                    }
                }
            }
        }
        return resolvedElements;
    }

    public SearchableTarget of(String... parameters) {
        List<String> instantiatedLocators = stream(cssOrXPathSelectors)
                                                  .map(locator -> instantiated(locator, parameters))
                                                  .collect(Collectors.toList());

        return new MultiXPathOrCssTarget(instantiated(targetElementName, parameters),
                                         iFrame,
                                         timeout,
                                         instantiatedLocators.toArray(new String[]{}));
    }

    @Override
    public String getCssOrXPathSelector() {
        throw new UnsupportedOperationException("The getCssOrXPathSelector() method is not supported for multi-locator Targets");
    }

    public Target called(String name) {
        return new MultiXPathOrCssTarget(name, iFrame, timeout, cssOrXPathSelectors);
    }

    @Override
    public Target waitingForNoMoreThan(Duration timeout) {
        return new MultiXPathOrCssTarget(targetElementName, iFrame, Optional.ofNullable(timeout), cssOrXPathSelectors);
    }

    @Override
    public List<By> selectors(WebDriver driver) {
        return stream(cssOrXPathSelectors).map(Selectors::xpathOrCssSelector).collect(Collectors.toList());
    }

    public List<String> getCssOrXPathSelectors() {
        return Arrays.asList(cssOrXPathSelectors);
    }

    private String instantiated(String cssOrXPathSelector, String[] parameters) {
        return new TargetSelectorWithVariables(cssOrXPathSelector).resolvedWith(parameters);
    }
}

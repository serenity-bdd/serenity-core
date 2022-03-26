package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.ListOfWebElementFacades;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.serenitybdd.core.selectors.Selectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.exceptions.ElementNotFoundAfterTimeoutError;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
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
        throw notifyUnfoundElement(effectiveTimeout.toMillis());
    }

    private ElementNotFoundAfterTimeoutError notifyUnfoundElement(long timeoutInMillis) {
        StepEventBus.getEventBus().notifyFailure();
        return new ElementNotFoundAfterTimeoutError("No element was found after " +
                (timeoutInMillis / 1000) + "s for " + getName() + System.lineSeparator()
                +"We tried with the following locators: " + Arrays.toString(cssOrXPathSelectors));

    }

    @Override
    public WebElementFacade resolveFor(SearchContext searchContext) {
        Optional<WebElementFacade> resolvedElement = findFirstMatching(searchContext);
        if (resolvedElement.isPresent()) {
            return resolvedElement.get();
        } else {
            throw notifyUnfoundElement(0);
        }
    }

    @Override
    public ListOfWebElementFacades resolveAllFor(SearchContext searchContext) {
        return new ListOfWebElementFacades(findAllMatching(searchContext, cssOrXPathSelectors));
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


    private Optional<WebElementFacade> findFirstMatching(SearchContext searchContext, String... cssOrXPathSelectors) {
        for(String selector : cssOrXPathSelectors) {
            List<WebElementFacade> matchingElements
                    = WebElementFacadeImpl.fromWebElements(searchContext.findElements(Selectors.xpathOrCssSelector(selector)));
            if (!matchingElements.isEmpty()) {
                return Optional.of(matchingElements.get(0));
            }
        }
        return Optional.empty();
    }


    private List<WebElementFacade> findAllMatching(SearchContext searchContext, String... cssOrXPathSelectors) {
        List<WebElementFacade> matchingElements = new ArrayList<>();
        for(String selector : cssOrXPathSelectors) {
            matchingElements.addAll(WebElementFacadeImpl.fromWebElements(searchContext.findElements(Selectors.xpathOrCssSelector(selector))));
        }
        return matchingElements;
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

    public ListOfWebElementFacades resolveAllFor(PageObject page) {
        List<WebElementFacade> resolvedElements =  findAllMatching(page, cssOrXPathSelectors);

        if (timeout.isPresent()) {
            if (resolvedElements.isEmpty()) {
                return new ListOfWebElementFacades(resolvedElements);
            } else {
                Duration effectiveTimeout = timeout.orElse(page.getImplicitWaitTimeout());
                long maxTimeAllowed = effectiveTimeout.toMillis();
                long timeStarted = System.currentTimeMillis();
                while (System.currentTimeMillis() - timeStarted < maxTimeAllowed) {
                    resolvedElements = findAllMatching(page, cssOrXPathSelectors);
                    if (!resolvedElements.isEmpty()) {
                        return new ListOfWebElementFacades(resolvedElements);
                    }
                }
            }
        }
        return new ListOfWebElementFacades(resolvedElements);
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

    public MultiXPathOrCssTarget called(String name) {
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

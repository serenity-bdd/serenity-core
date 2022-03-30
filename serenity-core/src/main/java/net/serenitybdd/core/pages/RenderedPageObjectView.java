package net.serenitybdd.core.pages;

import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.scheduling.NormalFluentWait;
import net.thucydides.core.scheduling.ThucydidesFluentWait;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.stubs.WebElementFacadeStub;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.serenitybdd.core.pages.FindAllWaitOptions.WITH_NO_WAIT;
import static net.serenitybdd.core.pages.FindAllWaitOptions.WITH_WAIT;
import static net.serenitybdd.core.selectors.Selectors.formattedXpathOrCssSelector;
import static net.serenitybdd.core.selectors.Selectors.xpathOrCssSelector;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;


/**
 * A page view that handles checking and waiting for element visibility.
 */
public class RenderedPageObjectView {

    private final transient WebDriver driver;
    private transient Duration waitForTimeout;
    private final Clock webdriverClock;
    private final Sleeper sleeper;
    private final PageObject pageObject;
    private final boolean timeoutCanBeOverriden;

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RenderedPageObjectView.class);

    public RenderedPageObjectView(final WebDriver driver, final PageObject pageObject, long waitForTimeoutInMilliseconds) {
        this(driver, pageObject, Duration.ofMillis(waitForTimeoutInMilliseconds), true);
    }

    public RenderedPageObjectView(final WebDriver driver, final PageObject pageObject, Duration waitForTimeout, boolean timeoutCanBeOverriden) {

        this.driver = driver;
        this.pageObject = pageObject;
        setWaitForTimeout(waitForTimeout);
        this.webdriverClock = Clock.systemDefaultZone();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
        this.timeoutCanBeOverriden = timeoutCanBeOverriden;
    }

    private boolean driverIsDisabled() {
        return StepEventBus.getEventBus().webdriverCallsAreSuspended();
    }

    public ThucydidesFluentWait<WebDriver> waitForCondition() {
        return new NormalFluentWait(driver, webdriverClock, sleeper)
                .withTimeout(waitForTimeout)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class,
                        NoSuchFrameException.class,
                        StaleElementReferenceException.class,
                        InvalidElementStateException.class);
    }

    public FluentWait<WebDriver> doWait() {
        return new FluentWait(driver)
                .withTimeout(waitForTimeout)
                .pollingEvery(Duration.ofMillis(WAIT_FOR_ELEMENT_PAUSE_LENGTH))
                .ignoreAll(NewList.of(NoSuchElementException.class,
                        NoSuchFrameException.class,
                        StaleElementReferenceException.class,
                        InvalidElementStateException.class));
    }

    private ExpectedCondition<Boolean> elementDisplayed(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (elementIsCurrentlyVisible(byElementCriteria));
            }

            @Override
            public String toString() {
                return "Expecting element is displayed: " + byElementCriteria.toString();
            }
        };
    }

    /**
     * This method will wait until an element is present and visible on the screen.
     */
    public WebElementFacade waitFor(final By byElementCriteria) {
        if (!driverIsDisabled()) {
            waitForCondition().until(elementDisplayed(byElementCriteria));
            return pageObject.find(byElementCriteria);
        } else {
            return new WebElementFacadeStub();
        }
    }

    public <T> T waitFor(final ExpectedCondition<T> expectedCondition) {
        return doWait().until(expectedCondition);
    }

    public <T> T waitFor(String message, final ExpectedCondition<T> expectedCondition) {
        return doWait().until(new WaitForWithMessage<>(message, expectedCondition));
    }

    public WebElementFacade waitFor(String xpathOrCssSelector) {
        return waitFor(xpathOrCssSelector(xpathOrCssSelector));
    }


    public WebElementFacade waitFor(final WebElement webElement) {
        return (webElement instanceof WebElementFacade) ?
                waitForElement((WebElementFacade) webElement) :
                waitForElement(pageObject.element(webElement));
    }

    public WebElementFacade waitFor(final WebElementFacade webElement) {
        return waitForElement(webElement);
    }

    public List<WebElementFacade> waitFor(final List<WebElementFacade> webElements) {
        return waitForElements(webElements);
    }

    private WebElementFacade waitForElement(final WebElementFacade webElement) {
        if (!driverIsDisabled()) {
            waitForCondition().until(elementIsDisplayed(webElement));
        }
        return webElement;
    }

    private List<WebElementFacade> waitForElements(final List<WebElementFacade> elements) {
        if (!driverIsDisabled()) {
            waitForCondition().until(elementsAreDisplayed(elements));
        }
        return elements;
    }

    private Function<? super WebDriver, Boolean> elementIsDisplayed(final WebElementFacade webElement) {
        return (ExpectedCondition<Boolean>) driver -> {
            try {
                if (webElement.isCurrentlyVisible()) {
                    return true;
                }
            } catch (NoSuchElementException noSuchElement) {
                // Ignore exception
            }
            return false;
        };
    }

    private boolean elementIsNotDisplayed(final By byElementCriteria) {
        List<WebElementFacade> matchingElements = findAllWithNoWait(byElementCriteria);
        for (WebElementFacade matchingElement : matchingElements) {
            if (matchingElement.isCurrentlyVisible()) {
                return false;
            }
        }
        return true;
    }

    private Function<? super WebDriver, Boolean> elementsAreDisplayed(final List<WebElementFacade> webElements) {
        return (ExpectedCondition<Boolean>) driver -> {
            try {
                return (webElements.size() > 0) && allElementsVisibleIn(webElements);
            } catch (NoSuchElementException noSuchElement) {
                // Ignore exception
            }
            return false;
        };
    }

    private boolean allElementsVisibleIn(List<WebElementFacade> webElements) {
        for (WebElementFacade element : webElements) {
            if (!element.isCurrentlyVisible()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method will wait until an element is present on the screen, though not necessarily visible.
     */
    public WebElement waitForPresenceOf(final By byElementCriteria) {
        WebDriverWait wait = new WebDriverWait(driver, waitForTimeout);
        return wait.until(presenceOfElementLocated(byElementCriteria));
    }

    public boolean elementIsPresent(final By byElementCriteria) {
        boolean isDisplayed = true;
        try {
            List<WebElement> matchingElements = driver.findElements(byElementCriteria);
            if (matchingElements.isEmpty()) {
                isDisplayed = false;
            }
        } catch (NoSuchElementException noSuchElement) {
            isDisplayed = false;
        }
        return isDisplayed;
    }

    public boolean elementIsDisplayed(final By byElementCriteria) {
        try {
            waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(byElementCriteria));
            return true;
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException noSuchElement) {
            return false;
        }
    }

    public boolean elementIsCurrentlyVisible(final By byElementCriteria) {
        try {
            List<WebElement> matchingElements = driver.findElements(byElementCriteria);
            return (!matchingElements.isEmpty() && matchingElements.get(0).isDisplayed());
        } catch (NoSuchElementException | StaleElementReferenceException | TimeoutException noSuchElement) {
            return false;
        }
    }

    private ExpectedCondition<Boolean> textPresent(final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (containsText(expectedText));
            }

            @Override
            public String toString() {
                return "Expecting text present: '" + expectedText + "'";
            }
        };
    }

    public void waitForText(final String expectedText) {
        if (!driverIsDisabled()) {
            waitForCondition().until(textPresent(expectedText));
        }
    }

    public WebDriverWait thenWait() {
        return new WebDriverWait(driver, getWaitForTimeout());
    }


    private ExpectedCondition<Boolean> textPresentInElement(final WebElement element, final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (containsText(element, expectedText));
            }

            @Override
            public String toString() {
                return "Expecting text present in element: '" + expectedText + "'";
            }
        };
    }

    public void waitForText(final WebElement element, final String expectedText) {
        if (!driverIsDisabled()) {
            waitForCondition().until(textPresentInElement(element, expectedText));
        }
    }

    private ExpectedCondition<Boolean> titlePresent(final String expectedTitle) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return titleIs(expectedTitle);
            }

            @Override
            public String toString() {
                return "Expecting title present: '" + expectedTitle + "', but found '" + driver.getTitle() + "' instead.'";
            }
        };
    }

    public void waitForTitle(final String expectedTitle) {
        if (!driverIsDisabled()) {
            waitForCondition().until(titlePresent(expectedTitle));
        }
    }

    private boolean titleIs(final String expectedTitle) {
        return ((driver.getTitle() != null) && (driver.getTitle().equals(expectedTitle)));
    }

    public boolean containsText(final String textValue) {
        return driver.findElement(By.tagName("body")).getText().contains(textValue);
    }

    public boolean containsText(final WebElement element, final String textValue) {
        return element.getText().contains(textValue);
    }

    private ExpectedCondition<Boolean> textNotPresent(final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !containsText(expectedText);
            }

            @Override
            public String toString() {
                return "Expecting text not present: '" + expectedText + "'";
            }
        };
    }

    public void waitForTextToDisappear(final String expectedText, final long timeout) {
        if (!driverIsDisabled()) {
            waitForCondition()
                    .withTimeout(timeout, TimeUnit.MILLISECONDS)
                    .until(textNotPresent(expectedText));
        }
    }

    public void waitForTextToAppear(final String expectedText, final long timeout) {
        if (!driverIsDisabled()) {
            waitForCondition()
                    .withTimeout(timeout, TimeUnit.MILLISECONDS)
                    .until(textPresent(expectedText));
        }
    }

    private ExpectedCondition<Boolean> titleNotPresent(final String expectedTitle) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !titleIs(expectedTitle);
            }

            @Override
            public String toString() {
                return "Expecting title present: '" + expectedTitle + "', but found '" + driver.getTitle() + "' instead.";
            }
        };
    }

    public void waitForTitleToDisappear(final String expectedTitle) {
        if (!driverIsDisabled()) {
            waitForCondition().until(titleNotPresent(expectedTitle));
        }
    }

    private ExpectedCondition<Boolean> anyTextPresent(final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return pageContainsAny(expectedTexts);
            }
        };
    }

    public void waitForAnyTextToAppear(final String... expectedTexts) {
        if (!driverIsDisabled()) {
            waitForCondition().until(anyTextPresent(expectedTexts));
        }
    }

    private ExpectedCondition<Boolean> anyTextPresentInElement(final WebElement element, final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return elementContains(element, expectedTexts);
            }

            @Override
            public String toString() {
                return "Expecting any text present in element: '" + Arrays.toString(expectedTexts) + "'";
            }

        };
    }

    public void waitForAnyTextToAppear(final WebElement element, final String... expectedTexts) {
        if (!driverIsDisabled()) {
            waitForCondition().until(anyTextPresentInElement(element, expectedTexts));
        }
    }


    public void waitForAbsenceOf(String xpathOrCssSelector) {
        waitForElementsToDisappear(xpathOrCssSelector(xpathOrCssSelector));
    }


    private boolean elementContains(final WebElement element, final String... expectedTexts) {
        for (String expectedText : expectedTexts) {
            if (containsText(element, expectedText)) {
                return true;
            }
        }
        return false;
    }

    private boolean pageContainsAny(final String... expectedTexts) {
        for (String expectedText : expectedTexts) {
            if (containsText(expectedText)) {
                return true;
            }
        }
        return false;
    }

    private ExpectedCondition<Boolean> allTextPresent(final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return Arrays.stream(expectedTexts).allMatch(expectedText -> containsText(expectedText));
//
//                for (String expectedText : expectedTexts) {
//                    if (!containsText(expectedText)) {
//                        return false;
//                    }
//                }
//                return true;
            }

            @Override
            public String toString() {
                return "Expecting all texts present in element: '" + Arrays.toString(expectedTexts) + "'";
            }
        };
    }

    public void waitForAllTextToAppear(final String... expectedTexts) {
        if (!driverIsDisabled()) {
            waitForCondition().until(allTextPresent(expectedTexts));
        }
    }

    private ExpectedCondition<Boolean> elementNotDisplayed(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (elementIsNotDisplayed(byElementCriteria));
            }

            @Override
            public String toString() {
                return "Expecting element not displayed: " + byElementCriteria;
            }
        };
    }

    public void waitForElementsToDisappear(final By byElementCriteria) {
        if (!driverIsDisabled()) {
            waitForCondition().until(elementNotDisplayed(byElementCriteria));
        }
    }

    private ExpectedCondition<Boolean> anyElementPresent(final By... expectedElements) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                for (By expectedElement : expectedElements) {
                    if (elementIsDisplayed(expectedElement)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String toString() {
                return "Expecting any element present: " + Arrays.toString(expectedElements);
            }
        };
    }

    public void waitForAnyRenderedElementOf(final By[] expectedElements) {
        if (!driverIsDisabled()) {
            waitForCondition().until(anyElementPresent(expectedElements));
        }
    }

//    public void setWaitForTimeoutInMilliseconds(long waitForTimeoutInMilliseconds) {
//        setWaitForTimeout(new Duration(waitForTimeoutInMilliseconds,TimeUnit.MILLISECONDS));
//    }

    public void setWaitForTimeout(Duration waitForTimeout) {
        this.waitForTimeout = waitForTimeout;
    }

    public Duration getWaitForTimeout() {
        return waitForTimeout;
    }

    protected List<WebElementFacade> findAllWithOptionalWait(By bySelector, FindAllWaitOptions waitForOptions) {
        List<WebElementFacade> results;
        try {
            pageObject.setImplicitTimeout(0, ChronoUnit.SECONDS);
            if (timeoutCanBeOverriden) {
                overrideWaitForTimeoutTo( Duration.ofSeconds(0));
            }
            if (waitForOptions == WITH_WAIT) {
                waitFor(bySelector);
            }
            results = pageObject.findAll(bySelector);
            if (timeoutCanBeOverriden) {
                resetWaitForTimeout();
            }
            pageObject.resetImplicitTimeout();
        } catch (TimeoutException e) {
            return new ArrayList();
        }
        return results;
    }

    public List<WebElementFacade> findAll(By bySelector) {
        return findAllWithOptionalWait(bySelector, WITH_WAIT);
    }

    public List<WebElementFacade> findAllWithNoWait(By bySelector) {
        return findAllWithOptionalWait(bySelector, WITH_NO_WAIT);
    }

    private Duration oldWaitFor;

    private void overrideWaitForTimeoutTo(Duration duration) {
        oldWaitFor = waitForTimeout;
        setWaitForTimeout(duration);
    }

    private void resetWaitForTimeout() {
        setWaitForTimeout(oldWaitFor);
    }

    public List<WebElementFacade> findAll(String xpathOrCssSelector) {
        return findAll(xpathOrCssSelector(xpathOrCssSelector));
    }

    public List<WebElementFacade> findFirstMatching(List<String> xpathOrCssSelectors) {
        List<WebElementFacade> matchingElements = new ArrayList<>();
        for(String selector : xpathOrCssSelectors) {
            matchingElements = findAllWithNoWait(xpathOrCssSelector(selector));
            if (!matchingElements.isEmpty()) {
                break;
            }
        }
        return matchingElements;
    }
//
//    public static Function<PageObject, List<WebElementFacade>> containingTextAndMatchingCSS(String cssOrXPathLocator, String expectedText) {
//        return page -> page.withTimeoutOf(Duration.ZERO)
//                .findAll(cssOrXPathLocator)
//                .stream()
//                .filter(element -> element.getTextContent().contains(expectedText))
//                .collect(Collectors.toList());
//    }
//
//
//    public static Function<PageObject, List<WebElementFacade>> containingTextAndMatchingCSS(List<String> cssOrXPathLocators, String expectedText) {
//        return page -> page.withTimeoutOf(Duration.ZERO)
//                .findFirstMatching(cssOrXPathLocators)
//                .stream()
//                .filter(element -> element.getTextContent().contains(expectedText))
//                .collect(Collectors.toList());
//    }

    public WebElementFacade find(By bySelector) {
        waitFor(bySelector);
        pageObject.setImplicitTimeout(0, ChronoUnit.SECONDS);
        WebElementFacade result = pageObject.find(bySelector);
        pageObject.resetImplicitTimeout();
        return result;
    }

    public WebElementFacade find(String xpathOrCssSelector) {
        return find(xpathOrCssSelector(xpathOrCssSelector));
    }

    public WebElementFacade find(String xpathOrCssSelector, Object firstArgument, Object... arguments) {
        List<Object> args = new ArrayList<>();
        args.add(firstArgument);
        args.addAll(Arrays.asList(arguments));
        return find(formattedXpathOrCssSelector(xpathOrCssSelector, args.toArray()));
    }

    public <T extends WebElementFacade> T moveTo(String xpathOrCssSelector) {
        pageObject.withAction().moveToElement(pageObject.findBy(xpathOrCssSelector));
        return pageObject.findBy(xpathOrCssSelector);
    }

    public WebElementFacadeWait waitForElement() {
        return new WebElementFacadeWait(pageObject);
    }

    public WebElementFacadeWait waitForElementForUpTo(long timeoutInSeconds) {
        return new WebElementFacadeWait(pageObject, timeoutInSeconds);
    }
}

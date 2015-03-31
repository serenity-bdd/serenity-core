package net.serenitybdd.core.pages;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.thucydides.core.scheduling.NormalFluentWait;
import net.thucydides.core.scheduling.ThucydidesFluentWait;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.support.ui.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.serenitybdd.core.pages.Selectors.xpathOrCssSelector;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static ch.lambdaj.Lambda.convert;

/**
 * A page view that handles checking and waiting for element visibility.
 */
public class RenderedPageObjectView {

    private final transient WebDriver driver;
    private transient Duration waitForTimeout;
    private final Clock webdriverClock;
    private final Sleeper sleeper;
    private final PageObject pageObject;

    private static final int WAIT_FOR_ELEMENT_PAUSE_LENGTH = 50;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RenderedPageObjectView.class);

    public RenderedPageObjectView(final WebDriver driver, final PageObject pageObject, long waitForTimeoutInMilliseconds) {
        this(driver, pageObject, new Duration(waitForTimeoutInMilliseconds, TimeUnit.MILLISECONDS));
    }

    public RenderedPageObjectView(final WebDriver driver, final PageObject pageObject, Duration waitForTimeout) {

        this.driver = driver;
        this.pageObject = pageObject;
        setWaitForTimeout(waitForTimeout);
        this.webdriverClock = new SystemClock();
        this.sleeper = Sleeper.SYSTEM_SLEEPER;
    }

    public ThucydidesFluentWait<WebDriver> waitForCondition() {
        return new NormalFluentWait<>(driver, webdriverClock, sleeper)
                .withTimeout(waitForTimeout.in(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    public FluentWait<WebDriver> doWait() {
        return new FluentWait(driver)
                .withTimeout(waitForTimeout.in(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                    .pollingEvery(WAIT_FOR_ELEMENT_PAUSE_LENGTH, TimeUnit.MILLISECONDS)
                    .ignoring(NoSuchElementException.class, NoSuchFrameException.class);
    }

    private ExpectedCondition<Boolean> elementDisplayed(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (elementIsCurrentlyVisible(byElementCriteria));
            }
        };
    }

    private ExpectedCondition<Boolean> elementPresent(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (elementIsDisplayed(byElementCriteria));
            }
        };
    }
    /**
     * This method will wait until an element is present and visible on the screen.
     */
    public void waitFor(final By byElementCriteria) {
        waitForCondition().until(elementDisplayed(byElementCriteria));
    }

    public void waitFor(final ExpectedCondition expectedCondition) {
        doWait().until(expectedCondition);
    }

    public void waitFor(String xpathOrCssSelector) {
        waitFor(xpathOrCssSelector(xpathOrCssSelector));
    }


    public WebElementFacade waitFor(final WebElement webElement) {
        return (webElement instanceof WebElementFacade) ?
                waitForElement((WebElementFacade)webElement) :
                waitForElement(pageObject.element(webElement));
    }

    public List<WebElementFacade> waitFor(final List<WebElementFacade> webElements) {
        return waitForElements(webElements);
    }

    private WebElementFacade waitForElement(final WebElementFacade webElement) {
        waitForCondition().until(elementIsDisplayed(webElement));
        return webElement;
    }

    private List<WebElementFacade> waitForElements(final List<WebElementFacade> elements) {
        waitForCondition().until(elementsAreDisplayed(elements));
        return elements;
    }

    private Function<? super WebDriver, Boolean> elementIsDisplayed(final WebElementFacade webElement) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    if (webElement.isCurrentlyVisible()) {
                        return true;
                    }
                } catch (NoSuchElementException noSuchElement) {
                    LOGGER.trace("No such element " + noSuchElement);
                }
                return false;
            }
        };
    }

    private Function<? super WebDriver, Boolean> elementsAreDisplayed(final List<WebElementFacade> webElements) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    return (webElements.size() > 0) && allElementsVisibleIn(webElements);
                } catch (NoSuchElementException noSuchElement) {
                    LOGGER.trace("No such element " + noSuchElement);
                }
                return false;
            }
        };
    }

    private boolean allElementsVisibleIn(List<WebElementFacade> webElements) {
        for(WebElementFacade element: webElements) {
            if (!element.isCurrentlyVisible()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method will wait until an element is present on the screen, though not necessarily visible.
     */
    public void waitForPresenceOf(final By byElementCriteria) {
        WebDriverWait wait = new WebDriverWait(driver, waitForTimeout.in(TimeUnit.SECONDS));
        wait.until(ExpectedConditions.presenceOfElementLocated(byElementCriteria));
    }

    public boolean elementIsPresent(final By byElementCriteria) {
        boolean isDisplayed = true;
        try {
            List<WebElement> matchingElements = driver.findElements(byElementCriteria);
            if (matchingElements.isEmpty()) {
                isDisplayed = false;
            }
        } catch (NoSuchElementException noSuchElement) {
            LOGGER.trace("No such element " + noSuchElement);
            isDisplayed = false;
        }
        return isDisplayed;
    }

    public boolean elementIsDisplayed(final By byElementCriteria) {
        try {
            waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(byElementCriteria));
            return true;
        } catch (NoSuchElementException noSuchElement) {
            LOGGER.trace("No such element " + noSuchElement);
            return false;
        } catch (StaleElementReferenceException se) {
            LOGGER.trace("Element no longer attached to the DOM " + se);
            return false;
        } catch (TimeoutException iGuessItsNotThere) {
            return false;
        }
    }

    public boolean elementIsCurrentlyVisible(final By byElementCriteria) {
        try {
            List<WebElement> matchingElements = driver.findElements(byElementCriteria);
            return (!matchingElements.isEmpty() && matchingElements.get(0).isDisplayed());
        } catch (NoSuchElementException noSuchElement) {
            LOGGER.trace("No such element " + noSuchElement);
            return false;
        } catch (StaleElementReferenceException se) {
            LOGGER.trace("Element no longer attached to the DOM " + se);
            return false;
        } catch (TimeoutException iGuessItsNotThere) {
            return false;
        }
    }

    private ExpectedCondition<Boolean> textPresent(final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (containsText(expectedText));
            }
        };
    }

    public void waitForText(final String expectedText) {
        waitForCondition().until(textPresent(expectedText));
    }

    private ExpectedCondition<Boolean> textPresentInElement(final WebElement element, final String expectedText) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (containsText(element, expectedText));
            }
        };
    }

    public void waitForText(final WebElement element, final String expectedText) {
        waitForCondition().until(textPresentInElement(element, expectedText));
    }

    private ExpectedCondition<Boolean> titlePresent(final String expectedTitle) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return titleIs(expectedTitle);
            }
        };
    }

    public void waitForTitle(final String expectedTitle) {
        waitForCondition().until(titlePresent(expectedTitle));
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
        };
    }

    public void waitForTextToDisappear(final String expectedText, final long timeout) {
        waitForCondition()
                .withTimeout(timeout, TimeUnit.MILLISECONDS)
                .until(textNotPresent(expectedText));
    }

	public void waitForTextToAppear(final String expectedText, final long timeout) {
		waitForCondition()
				.withTimeout(timeout, TimeUnit.MILLISECONDS)
				.until(textPresent(expectedText));
	}

    private ExpectedCondition<Boolean> titleNotPresent(final String expectedTitle) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return !titleIs(expectedTitle);
            }
        };
    }

    public void waitForTitleToDisappear(final String expectedTitle) {
        waitForCondition().until(titleNotPresent(expectedTitle));
    }

    private ExpectedCondition<Boolean> anyTextPresent(final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return pageContainsAny(expectedTexts);
            }
        };
    }

    public void waitForAnyTextToAppear(final String... expectedTexts) {
        waitForCondition().until(anyTextPresent(expectedTexts));
    }

    private ExpectedCondition<Boolean> anyTextPresentInElement(final WebElement element, final String... expectedTexts) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return elementContains(element, expectedTexts);
            }
        };
    }

    public void waitForAnyTextToAppear(final WebElement element, final String... expectedTexts) {
        waitForCondition().until(anyTextPresentInElement(element, expectedTexts));
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
                for(String expectedText : expectedTexts) {
                    if (!containsText(expectedText)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public void waitForAllTextToAppear(final String... expectedTexts) {
        waitForCondition().until(allTextPresent(expectedTexts));
    }

    private ExpectedCondition<Boolean> elementNotDisplayed(final By byElementCriteria) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (!elementIsDisplayed(byElementCriteria));
            }
        };
    }

    public void waitForElementsToDisappear(final By byElementCriteria) {
        waitForCondition().until(elementNotDisplayed(byElementCriteria));
    }

    private ExpectedCondition<Boolean> anyElementPresent(final By... expectedElements) {
        return new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                for(By expectedElement : expectedElements) {
                    if (elementIsDisplayed(expectedElement)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
    public void waitForAnyRenderedElementOf(final By[] expectedElements) {
        waitForCondition().until(anyElementPresent(expectedElements));
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

    public List<net.serenitybdd.core.pages.WebElementFacade> findAll(By bySelector) {
        List<net.serenitybdd.core.pages.WebElementFacade> results;
        try {
            pageObject.setImplicitTimeout(0, TimeUnit.SECONDS);
            waitFor(bySelector);
            results = pageObject.findAll(bySelector);
            pageObject.resetImplicitTimeout();
        } catch (TimeoutException e) {
            return Lists.newArrayList();
        }
        return results;
    }

    public List<net.serenitybdd.core.pages.WebElementFacade> findAll(String xpathOrCssSelector) {
        return findAll(xpathOrCssSelector(xpathOrCssSelector));
    }

    public net.serenitybdd.core.pages.WebElementFacade find(By bySelector) {
        waitFor(bySelector);
        pageObject.setImplicitTimeout(0, TimeUnit.SECONDS);
        net.serenitybdd.core.pages.WebElementFacade result = pageObject.find(bySelector);
        pageObject.resetImplicitTimeout();
        return result;
    }

    public net.serenitybdd.core.pages.WebElementFacade find(String xpathOrCssSelector) {
        return find(xpathOrCssSelector(xpathOrCssSelector));
    }
}

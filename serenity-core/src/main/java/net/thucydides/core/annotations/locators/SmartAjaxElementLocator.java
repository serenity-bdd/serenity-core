package net.thucydides.core.annotations.locators;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.annotations.locators.SmartAnnotations;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.WebdriverCollectionStrategy;
import net.thucydides.core.annotations.ElementIsUsable;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import net.thucydides.core.webdriver.MobilePlatform;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import net.thucydides.core.webdriver.exceptions.ElementNotFoundAfterTimeoutError;
import net.thucydides.core.webdriver.exceptions.ElementNotVisibleAfterTimeoutError;
import net.thucydides.core.webdriver.stubs.WebElementFacadeStub;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.SlowLoadableComponent;
import org.openqa.selenium.support.ui.SystemClock;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmartAjaxElementLocator extends SmartElementLocator implements WithConfigurableTimeout {
    public static final Duration ZERO_SECONDS = new Duration(0, TimeUnit.SECONDS);
    protected Optional<Integer> annotatedTimeoutInSeconds;
    private final Clock clock;

    private final Field field;
    private final SearchContext searchContext;
    private final MobilePlatform platform;
    private final EnvironmentVariables environmentVariables;

    /**
     * Main constructor.
     *
     * @param searchContext    The SearchContext to use when locating elements
     * @param field            The field representing this element
     * @param timeOutInSeconds How long to wait for the element to appear. Measured in seconds.
     * @deprecated The timeOutInSeconds parameter is no longer used - implicit timeouts should now be used
     */
    public SmartAjaxElementLocator(SearchContext searchContext, Field field, MobilePlatform platform, int timeOutInSeconds) {
        this(new SystemClock(), searchContext, field, platform);

    }

    public SmartAjaxElementLocator(SearchContext searchContext, Field field, MobilePlatform platform) {
        this(new SystemClock(), searchContext, field, platform);

    }

    public SmartAjaxElementLocator(Clock clock, SearchContext searchContext, Field field, MobilePlatform platform) {
        super(searchContext, field, platform);
        this.annotatedTimeoutInSeconds = timeoutFrom(field);
        this.clock = clock;
        this.field = field;
        if (searchContext instanceof WebDriverFacade) {
            if (annotatedTimeoutInSeconds.isPresent()) {
                this.searchContext = ((WebDriverFacade) searchContext)
                        .withTimeoutOf(new Duration(annotatedTimeoutInSeconds.get(), TimeUnit.SECONDS));
            } else {
                this.searchContext = searchContext;
            }
        } else if (searchContext instanceof WebElementFacade) {
            if (annotatedTimeoutInSeconds.isPresent()) {
                this.searchContext = ((WebElementFacade) searchContext).withTimeoutOf(annotatedTimeoutInSeconds.get(), TimeUnit.SECONDS);
            } else {
                this.searchContext = searchContext;
            }
        } else if (searchContext instanceof WebDriver) {
            this.searchContext = WebdriverProxyFactory.getFactory().proxyFor((WebDriver) searchContext);
        } else {
            this.searchContext = searchContext;
        }
        this.platform = platform;
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
    }

    private Optional<Integer> timeoutFrom(Field field) {
        FindBy findBy = field.getAnnotation(FindBy.class);
        if ((findBy != null) && (StringUtils.isNotEmpty(findBy.timeoutInSeconds()))) {
            return Optional.of(Integer.valueOf(findBy.timeoutInSeconds()));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public WebElement findElement() {
        if (aPreviousStepHasFailed()) {
            return new WebElementFacadeStub();
        } else if (shouldFindElementImmediately()) {
            return findElementImmediately();
        } else {
            return ajaxFindElement();
        }
    }

    @Deprecated
    public void setTimeOutInSeconds(int timeOutInSeconds) {
    }

    private boolean shouldFindElementImmediately() {
        return /*aPreviousStepHasFailed() ||*/ (MethodTiming.forThisThread().isInQuickMethod());
    }

    public WebElement findElementImmediately() {
        SmartAnnotations annotations = new SmartAnnotations(field, platform);
        By by = annotations.buildBy();
        if (searchContext instanceof ConfigurableTimeouts) {
            ((ConfigurableTimeouts) searchContext).setImplicitTimeout(ZERO_SECONDS);
        }

        WebElement element;
        try {
            element = searchContext.findElement(by);
        } catch (Throwable e) {
            throw e;
        } finally {
            if (searchContext instanceof ConfigurableTimeouts) {
                ((ConfigurableTimeouts) searchContext).resetTimeouts();
            }
        }
        if (element == null) {
            throw new NoSuchElementException("No such element found for criteria " + by.toString());
        }
        return element;
    }

    /**
     * Will poll the interface on a regular basis until the element is present.
     */
    public WebElement ajaxFindElement() {
        SlowLoadingElement loadingElement = new SlowLoadingElement(clock, annotatedTimeoutInSeconds.or(getTimeOutInSeconds()));
        try {
            return loadingElement.get().getElement();
        } catch (ElementNotVisibleAfterTimeoutError notVisible) {
            throw new ElementNotVisibleException(
                    String.format("Timed out after %d seconds. %s", annotatedTimeoutInSeconds.or(getTimeOutInSeconds()), notVisible.getMessage()),
                    notVisible.getCause());
        } catch (Error e) {
            throw new NoSuchElementException(
                    String.format("Timed out after %d seconds. %s", annotatedTimeoutInSeconds.or(getTimeOutInSeconds()), e.getMessage()));
        }
    }

    private int getTimeOutInSeconds() {
        if (searchContext instanceof WebDriverFacade) {
            return (int) ((WebDriverFacade) searchContext).getCurrentImplicitTimeout().in(TimeUnit.SECONDS);
        }
        if (searchContext instanceof WebElementFacade) {
            return (int) ((WebElementFacade) searchContext).getCurrentImplicitTimeout().in(TimeUnit.SECONDS);
        }
        return 0;
    }

    private final static List<WebElement> EMPTY_LIST_OF_WEBELEMENTS = Lists.newArrayList();

    /**
     * Will poll the interface on a regular basis until at least one element is present.
     */
    public List<WebElement> findElements() {
        if (aPreviousStepHasFailed()) {
            return EMPTY_LIST_OF_WEBELEMENTS;
        }
        SlowLoadingElementList list = new SlowLoadingElementList(clock, annotatedTimeoutInSeconds.or(getTimeOutInSeconds()));
        try {
            return list.get().getElements();
        } catch (Error e) {
            throw new NoSuchElementException(
                    String.format("Timed out after %d seconds. %s", annotatedTimeoutInSeconds.or(getTimeOutInSeconds()), e.getMessage()),
                    e.getCause());
        }
    }


    private boolean aPreviousStepHasFailed() {
        return (StepEventBus.getEventBus().aStepInTheCurrentTestHasFailed());
    }

    /**
     * By default, we sleep for 250ms between polls. You may override this method in order to change
     * how it sleeps.
     *
     * @return Duration to sleep in milliseconds
     */
    protected long sleepFor() {
        return 250;
    }

    private class SlowLoadingElement extends SlowLoadableComponent<SlowLoadingElement> {
        private Optional<WebDriverException> lastException = Optional.absent();
        private WebElement element;

        public SlowLoadingElement(Clock clock, int timeOutInSeconds) {
            super(clock, timeOutInSeconds);
        }

        @Override
        protected void load() {
            lastException = Optional.absent();
            if (element == null) {
                try {
                    element = SmartAjaxElementLocator.super.findElement();
                } catch (WebDriverException e) {
                    lastException = Optional.of(e);
                }
            }
        }

        @Override
        protected long sleepFor() {
            return SmartAjaxElementLocator.this.sleepFor();
        }

        @Override
        protected void isLoaded() throws Error {
            if (element != null) {
                load();
            }
            if (!ElementIsUsable.forElement(element)) {
                if (lastException.isPresent()) {
                    throw new ElementNotFoundAfterTimeoutError("Element not found", lastException.get());
                } else {
                    throw new ElementNotVisibleAfterTimeoutError("Element not available");
                }
            }
        }

//        @Override
//        protected void isError() throws Error {
//            if (lastException.isPresent()) {
//                throw new AssertionError("Element could not be loaded", lastException.get());
//            }
//        }

//		public NoSuchElementException getLastException() {
//			return lastException.get();
//		}

        public WebElement getElement() {
            return element;
        }
    }

    private class SlowLoadingElementList extends SlowLoadableComponent<SlowLoadingElementList> {
        private Optional<WebDriverException> lastException = Optional.absent();
        private List<WebElement> elements;

        public SlowLoadingElementList(Clock clock, int timeOutInSeconds) {
            super(clock, timeOutInSeconds);
        }

        @Override
        protected void load() {
            lastException = Optional.absent();
            if (elements == null) {
                try {
                    elements = SmartAjaxElementLocator.super.findElements();
                } catch (WebDriverException e) {
                    lastException = Optional.of(e);
                }
            }
        }

        @Override
        protected long sleepFor() {
            return SmartAjaxElementLocator.this.sleepFor();
        }

        @Override
        protected void isLoaded() throws Error {
            if (elements == null) {
                load();
            }
            if (!areElementsUsable(elements)) {
                if (lastException.isPresent()) {
                    throw new ElementNotFoundAfterTimeoutError("List elements not found", lastException.get());
                } else {
                    throw new ElementNotVisibleAfterTimeoutError("List elements not visible");
                }
            }
        }

        private boolean areElementsUsable(List<WebElement> elements) {
            WebdriverCollectionStrategy collectionStrategy = WebdriverCollectionStrategy.definedIn(environmentVariables);
            return WaitForWebElementCollection.accordingTo(collectionStrategy).areElementsReadyIn(elements);

        }

        public List<WebElement> getElements() {
            return elements;
        }

        @Override
        public String toString() {
            SmartAnnotations annotations = new SmartAnnotations(field, platform);
            By by = annotations.buildBy();
            return by.toString();
        }
    }

    @Override
    public String toString() {
        return (field != null) ? field.getDeclaringClass().getSimpleName() + "." + field.getName() : "";
    }
}
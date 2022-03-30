package net.thucydides.core.annotations.locators;

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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.SlowLoadableComponent;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;

import static net.thucydides.core.annotations.locators.SearchContextType.*;

public class SmartAjaxElementLocator extends SmartElementLocator implements WithConfigurableTimeout {
    private static final Duration ZERO_SECONDS =  Duration.ofSeconds(0);
    private Optional<Integer> annotatedTimeoutInSeconds;
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
     */
    public SmartAjaxElementLocator(SearchContext searchContext, Field field, MobilePlatform platform) {
        this(Clock.systemDefaultZone(), searchContext, field, platform);

    }

    private interface SearchContextProvider extends BiFunction<SearchContext, java.util.Optional<Integer>, SearchContext> {}

    private final static SearchContextProvider UNMODIFIED_SEARCH_CONTEXT = (context, timeout) -> context;

    private final static SearchContextProvider WEB_DRIVER_FACADE = (context, timeout) -> WebdriverProxyFactory.getFactory().proxyFor((WebDriver) context);

    private final static SearchContextProvider WEB_ELEMENT_FACADE_WITH_OPTIONAL_TIMEOUT = (context, timeout) -> {
        if (timeout.isPresent()) {
            return ((WebElementFacade) context).withTimeoutOf(timeout.get(), ChronoUnit.SECONDS);
        } else {
            return context;
        }
    };

    private final static SearchContextProvider WEBELEMENT_SEARCH_CONTEXT = (context, timeout) -> context;


    private static Map<SearchContextType, SearchContextProvider> SEARCH_CONTEXTS = new HashMap<>();

    static {
        SEARCH_CONTEXTS.put(WebDriverFacadeContext,     UNMODIFIED_SEARCH_CONTEXT);
        SEARCH_CONTEXTS.put(WebElementFacadeContext,    WEB_ELEMENT_FACADE_WITH_OPTIONAL_TIMEOUT);
        SEARCH_CONTEXTS.put(WebDriverContext,           WEB_DRIVER_FACADE);
        SEARCH_CONTEXTS.put(WebElementContext,          WEBELEMENT_SEARCH_CONTEXT);
    }

    SmartAjaxElementLocator(Clock clock, SearchContext searchContext, Field field, MobilePlatform platform) {
        super(searchContext, field, platform);
        this.annotatedTimeoutInSeconds = timeoutFrom(field);
        this.clock = clock;
        this.field = field;

        this.searchContext = SEARCH_CONTEXTS.get(typeOf(searchContext)).apply(searchContext, annotatedTimeoutInSeconds);
        this.platform = platform;
        this.environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
    }


    private SearchContextType typeOf(SearchContext searchContext) {
        if (searchContext instanceof WebDriverFacade) return WebDriverFacadeContext;
        if (searchContext instanceof WebDriver) return WebDriverContext;
        if (searchContext instanceof WebElementFacade) return WebElementFacadeContext;
        if (searchContext instanceof WebElement) return WebElementFacadeContext;
        return OtherContext;
    }


    private java.util.Optional<Integer> timeoutFrom(Field field) {
        FindBy findBy = field.getAnnotation(FindBy.class);
        if ((findBy != null) && (StringUtils.isNotEmpty(findBy.timeoutInSeconds()))) {
            return java.util.Optional.of(Integer.valueOf(findBy.timeoutInSeconds()));
        } else {
            return java.util.Optional.empty();
        }
    }

    @Override
    public WebElement findElement() {
        if (inADisabledStep()) {
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
        SlowLoadingElement loadingElement = new SlowLoadingElement(clock, annotatedTimeoutInSeconds.orElse(getTimeOutInSeconds()));
        try {
            return loadingElement.get().getElement();
        } catch (ElementNotVisibleAfterTimeoutError notVisible) {
            throw new ElementNotInteractableException(
                    String.format("Timed out after %d seconds. %s", annotatedTimeoutInSeconds.orElse(getTimeOutInSeconds()), notVisible.getMessage()),
                    notVisible.getCause());
        } catch (Error e) {
            throw new NoSuchElementException(
                    String.format("Timed out after %d seconds. %s", annotatedTimeoutInSeconds.orElse(getTimeOutInSeconds()), e.getMessage()));
        }
    }

    private int getTimeOutInSeconds() {
        if (searchContext instanceof WebDriverFacade) {
            return (int) ((WebDriverFacade) searchContext).getCurrentImplicitTimeout().getSeconds();
        }
        if (searchContext instanceof WebElementFacade) {
            return (int) ((WebElementFacade) searchContext).getCurrentImplicitTimeout().getSeconds();
        }
        return 0;
    }

    private final static List<WebElement> EMPTY_LIST_OF_WEBELEMENTS = new ArrayList<>();

    /**
     * Will poll the interface on a regular basis until at least one element is present.
     */
    public List<WebElement> findElements() {
        if (inADisabledStep()) {
            return EMPTY_LIST_OF_WEBELEMENTS;
        }
        SlowLoadingElementList list = new SlowLoadingElementList(clock, annotatedTimeoutInSeconds.orElse(getTimeOutInSeconds()));
        try {
            return list.get().getElements();
        } catch (Error e) {
            throw new NoSuchElementException(
                    String.format("Timed out after %d seconds. %s", annotatedTimeoutInSeconds.orElse(getTimeOutInSeconds()), e.getMessage()),
                    e.getCause());
        }
    }


    private boolean inADisabledStep() {
        return (StepEventBus.getEventBus().webdriverCallsAreSuspended());
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
        private Optional<WebDriverException> lastException = Optional.empty();
        private WebElement element;

        public SlowLoadingElement(Clock clock, int timeOutInSeconds) {
            super(clock, timeOutInSeconds);
        }

        @Override
        protected void load() {
            lastException = Optional.empty();
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

        public WebElement getElement() {
            return element;
        }
    }

    private class SlowLoadingElementList extends SlowLoadableComponent<SlowLoadingElementList> {
        private Optional<WebDriverException> lastException = Optional.empty();
        private List<WebElement> elements;

        public SlowLoadingElementList(Clock clock, int timeOutInSeconds) {
            super(clock, timeOutInSeconds);
        }

        @Override
        protected void load() {
            lastException = Optional.empty();
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

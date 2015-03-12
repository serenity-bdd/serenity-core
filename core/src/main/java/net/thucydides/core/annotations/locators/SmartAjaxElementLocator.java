package net.thucydides.core.annotations.locators;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.annotations.locators.SmartAnnotations;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.ConfigurableTimeouts;
import net.thucydides.core.webdriver.MobilePlatform;

import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.SlowLoadableComponent;
import org.openqa.selenium.support.ui.SystemClock;

import com.google.common.collect.Lists;

public class SmartAjaxElementLocator extends SmartElementLocator implements WithConfigurableTimeout {
	public static final Duration ZERO_SECONDS = new Duration(0, TimeUnit.SECONDS);
	protected int timeOutInSeconds;
	protected Optional<Integer> annotatedTimeoutInSecondes;
	private final Clock clock;

	private final Field field;
	private final SearchContext searchContext;
    private final MobilePlatform platform;

	/**
	 * Main constructor.
	 *
	 * @param searchContext The SearchContext to use when locating elements
	 * @param field The field representing this element
	 * @param timeOutInSeconds How long to wait for the element to appear. Measured in seconds.
	 */
    public SmartAjaxElementLocator(SearchContext searchContext, Field field, MobilePlatform platform, int timeOutInSeconds) {
        this(new SystemClock(), searchContext, field, platform, timeOutInSeconds);

	}

    public SmartAjaxElementLocator(Clock clock, SearchContext searchContext, Field field, MobilePlatform platform, Integer defaultTimeoutInSeconds) {
        super(searchContext, field, platform);
		this.timeOutInSeconds = defaultTimeoutInSeconds;
		this.annotatedTimeoutInSecondes = timeoutFrom(field);
		this.clock = clock;
		this.field = field;
		if ((searchContext instanceof WebDriverFacade) || (searchContext instanceof WebElementFacade)) {
			this.searchContext = searchContext;
		} else if (searchContext instanceof WebDriver) {
			this.searchContext = WebdriverProxyFactory.getFactory().proxyFor((WebDriver) searchContext);
		} else {
			this.searchContext = searchContext;
		}


        this.platform = platform;
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
		if (shouldFindElementImmediately()) {
			return findElementImmediately();
		} else {
			return ajaxFindElement();
		}
	}

	public void setTimeOutInSeconds(int timeOutInSeconds) {
		this.timeOutInSeconds = timeOutInSeconds;
	}

	private boolean shouldFindElementImmediately() {
		return aPreviousStepHasFailed() || (calledFromAQuickMethod());
	}

	private boolean calledFromAQuickMethod() {
        for (StackTraceElement elt : Thread.currentThread().getStackTrace()) {
            //if (QUICK_METHODS.contains(elt.getName())
            if (elt.getMethodName().contains("Currently")) {
                return true;
            }
        }
        return false;
    }

	public WebElement findElementImmediately() {
        SmartAnnotations annotations = new SmartAnnotations(field, platform);
		By by = annotations.buildBy();
		if (searchContext instanceof ConfigurableTimeouts) {
			((ConfigurableTimeouts) searchContext).setImplicitTimeout(ZERO_SECONDS);
		}
		WebElement element = searchContext.findElement(by);
		if (searchContext instanceof ConfigurableTimeouts) {
			((ConfigurableTimeouts) searchContext).resetTimeouts();
		}
		if (element == null) {
			throw new NoSuchElementException("No such element found for criteria " + by.toString());
		} 
		return element;
	}

	protected boolean isElementUsable(WebElement element) {
		return (element != null) && (element.isDisplayed());
	}

	/**
	 * Will poll the interface on a regular basis until the element is present.
	 */
	public WebElement ajaxFindElement() {
		SlowLoadingElement loadingElement = new SlowLoadingElement(clock, annotatedTimeoutInSecondes.or(timeOutInSeconds));
		try {
			return loadingElement.get().getElement();
		} catch (NoSuchElementError e) {
			throw new NoSuchElementException(
					String.format("Timed out after %d seconds. %s", annotatedTimeoutInSecondes.or(timeOutInSeconds), e.getMessage()),
					e.getCause());
		}
	}

    private final static List<WebElement> EMPTY_LIST_OF_WEBELEMENTS = Lists.newArrayList();

	/**
	 * Will poll the interface on a regular basis until at least one element is present.
	 */
	public List<WebElement> findElements() {
        if (aPreviousStepHasFailed()) {
            return EMPTY_LIST_OF_WEBELEMENTS;
        }
		SlowLoadingElementList list = new SlowLoadingElementList(clock, annotatedTimeoutInSecondes.or(timeOutInSeconds));
		try {
			return list.get().getElements();
		} catch (NoSuchElementError e) {
			throw new NoSuchElementException(
					String.format("Timed out after %d seconds. %s", annotatedTimeoutInSecondes.or(timeOutInSeconds), e.getMessage()),
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
		private NoSuchElementException lastException;
		private WebElement element;

		public SlowLoadingElement(Clock clock, int timeOutInSeconds) {
			super(clock, timeOutInSeconds);
		}

		@Override
		protected void load() {
			// Does nothing
		}

		@Override
		protected long sleepFor() {
			return SmartAjaxElementLocator.this.sleepFor();
		}

		@Override
		protected void isLoaded() throws Error {
			try {
				element = SmartAjaxElementLocator.super.findElement();
				if (!isElementUsable(element)) {
					throw new NoSuchElementException("Element is not usable " + element.toString());
				}
			} catch (NoSuchElementException e) {
				lastException = e;
				// Should use JUnit's AssertionError, but it may not be present
				throw new NoSuchElementError("Unable to locate the element: " + e.getMessage(), e);
			}
		}

		public NoSuchElementException getLastException() {
			return lastException;
		}

		public WebElement getElement() {
			return element;
		}
	}

	private class SlowLoadingElementList extends SlowLoadableComponent<SlowLoadingElementList> {
		private NoSuchElementException lastException;
		private List<WebElement> elements;

		public SlowLoadingElementList(Clock clock, int timeOutInSeconds) {
			super(clock, timeOutInSeconds);
		}

		@Override
		protected void load() {
			// Does nothing
		}

		@Override
		protected long sleepFor() {
			return SmartAjaxElementLocator.this.sleepFor();
		}

		@Override
		protected void isLoaded() throws Error {
			try {
				elements = SmartAjaxElementLocator.super.findElements();
				if (elements.size() == 0) {
					/*return even if empty and don't wait for them to become available.
					*not sure that it is the correct approach for Ajax Element Locator that should wait for elements
					*however correcting it due to https://java.net/jira/browse/THUCYDIDES-187 */
					return; 
				}
				for (WebElement element : elements) {
					if (!isElementUsable(element)) {
						throw new NoSuchElementException("Element is not usable");
					}
				}
			} catch (NoSuchElementException e) {
				lastException = e;
				// Should use JUnit's AssertionError, but it may not be present
				throw new NoSuchElementError("Unable to locate the element " + e.getMessage(), e);
			}
		}

		public NoSuchElementException getLastException() {
			return lastException;
		}

		public List<WebElement> getElements() {
			return elements;
		}
	}

	private static class NoSuchElementError extends Error {
		private NoSuchElementError(String message, Throwable throwable) {
			super(message, throwable);
		}
	}

    @Override
    public String toString() {
        SmartAnnotations annotations = new SmartAnnotations(field, platform);
        By by = annotations.buildBy();
        return by.toString();
    }
}

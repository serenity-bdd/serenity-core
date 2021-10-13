package net.serenitybdd.screenplay.targets;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.webdriver.exceptions.ElementNotFoundAfterTimeoutError;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LambdaTarget extends Target {

    private final Function<PageObject, List<WebElementFacade>> locationStrategy;

    public LambdaTarget(String targetElementName, Function<PageObject, List<WebElementFacade>> locationStrategy, Optional<IFrame> iFrame) {
        super(targetElementName, iFrame);
        this.locationStrategy = locationStrategy;
    }

    public LambdaTarget(String targetElementName, Function<PageObject, List<WebElementFacade>> locationStrategy, Optional<IFrame> iFrame, Optional<Duration> timeout) {
        super(targetElementName, iFrame, timeout);
        this.locationStrategy = locationStrategy;
    }

    private Throwable lastThrownException;

    public WebElementFacade resolveFor(PageObject page) {

        Duration effectiveTimeout = timeout.orElse(page.getImplicitWaitTimeout());

        Optional<WebElementFacade> resolvedElement = resolveElementFor(page);
        if (resolvedElement.isPresent()) {
            return resolvedElement.get();
        } else {
            long maxTimeAllowed = effectiveTimeout.toMillis();
            long timeStarted = System.currentTimeMillis();
            while (System.currentTimeMillis() - timeStarted < maxTimeAllowed) {
                resolvedElement = resolveElementFor(page);
                if (resolvedElement.isPresent()) {
                    return resolvedElement.get();
                }
            }
        }
        if (!resolvedElement.isPresent()) {
            String errorMessage = "No element was found after " + (effectiveTimeout.toMillis() / 1000) + "s for " + getName();
            if (lastThrownException != null) {
                throw new ElementNotFoundAfterTimeoutError(errorMessage, lastThrownException);
            } else {
                throw new ElementNotFoundAfterTimeoutError(errorMessage);
            }
        }

        return resolvedElement.get();
    }

    @NotNull
    private Optional<WebElementFacade> resolveElementFor(PageObject page) {
        this.lastThrownException = null;
        try {
            return locationStrategy.apply(page).stream().findFirst();
        } catch (Throwable exception) {
            this.lastThrownException = exception;
            return Optional.empty();
        }
    }


    public List<WebElementFacade> resolveAllFor(PageObject page) {
        List<WebElementFacade> resolvedElements = locationStrategy.apply(page);

        if (timeout.isPresent()) {
            if (resolvedElements.isEmpty()) {
                return resolvedElements;
            } else {
                Duration effectiveTimeout = timeout.orElse(page.getImplicitWaitTimeout());
                long maxTimeAllowed = effectiveTimeout.toMillis();
                long timeStarted = System.currentTimeMillis();
                while (System.currentTimeMillis() - timeStarted < maxTimeAllowed) {
                    resolvedElements = locationStrategy.apply(page);
                    if (!resolvedElements.isEmpty()) {
                        return resolvedElements;
                    }
                }
            }
        }
        return resolvedElements;
    }

    public SearchableTarget of(String... parameters) {
        throw new UnsupportedOperationException("The of() method is not supported for By-type Targets");
    }

    @Override
    public String getCssOrXPathSelector() {
        throw new UnsupportedOperationException("The getCssOrXPathSelector() method is not supported for lambda-type Targets");
    }

    @Override
    public Target waitingForNoMoreThan(Duration timeout) {
        return new LambdaTarget(targetElementName, locationStrategy, iFrame, Optional.ofNullable(timeout));
    }

    @Override
    public List<By> selectors(WebDriver driver) {
        throw new UnsupportedOperationException("Nested locators are not supported with this kind of locator: " + this);
    }

    public LambdaTarget called(String name) {
        return new LambdaTarget(name, locationStrategy, iFrame, timeout);
    }
}

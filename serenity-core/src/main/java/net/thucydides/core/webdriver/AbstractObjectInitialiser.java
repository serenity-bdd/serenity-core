package net.thucydides.core.webdriver;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public abstract class AbstractObjectInitialiser<T> implements Predicate<T> {

    protected final WebDriver driver;
    protected final Duration ajaxTimeout;

    public AbstractObjectInitialiser(WebDriver driver, long ajaxTimeoutInMilliseconds) {
        this.driver = driver;
        this.ajaxTimeout = Duration.ofMillis(ajaxTimeoutInMilliseconds);
    }

    protected int ajaxTimeoutInSecondsWithAtLeast1Second() {
        return (int) ((ajaxTimeout.getSeconds() > 0) ? ajaxTimeout.getSeconds() : 1);
    }

    @Override
    public abstract boolean apply(T input);

    public boolean test(T input) {
        return apply(input);
    }

}

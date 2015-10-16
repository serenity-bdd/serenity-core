package net.thucydides.core.webdriver;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Duration;

import java.util.concurrent.TimeUnit;

public abstract class AbstractObjectInitialiser<T> implements Predicate<T> {
	
    protected final WebDriver driver;
    protected final Duration ajaxTimeout;

    public AbstractObjectInitialiser(WebDriver driver, long ajaxTimeoutInMilliseconds) {
        this.driver = driver;
        this.ajaxTimeout = new Duration(ajaxTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
    }

    protected int ajaxTimeoutInSecondsWithAtLeast1Second() {
        return (int) ((ajaxTimeout.in(TimeUnit.SECONDS) > 0) ? ajaxTimeout.in(TimeUnit.SECONDS) : 1);
    }
    
	@Override
	public abstract boolean apply(T input);
}

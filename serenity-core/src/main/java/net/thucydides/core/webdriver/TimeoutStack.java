package net.thucydides.core.webdriver;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Duration;

import java.util.Map;
import java.util.Stack;

/**
 * Created by john on 12/03/15.
 */
public class TimeoutStack {

    Map<WebDriver, Stack<Duration>> timeouts = Maps.newHashMap();

    public void pushTimeoutFor(WebDriver driver, Duration implicitTimeout) {
        if (!timeouts.containsKey(driver)) {
            timeouts.put(driver, new Stack<Duration>());
        }
        timeouts.get(driver).push(implicitTimeout);
    }

    public Optional<Duration> popTimeoutFor(WebDriver driver) {
        if (timeouts.containsKey(driver)) {
            return timeouts.get(driver).isEmpty() ? Optional.<Duration>absent() :
                                                           Optional.of(timeouts.get(driver).pop());
        } else {
            return Optional.absent();
        }
    }

    public Boolean containsTimeoutFor(WebDriver driver) {
        return timeouts.containsKey(driver) && !timeouts.get(driver).isEmpty();
    }

    public Optional<Duration> currentTimeoutValueFor(WebDriver driver) {
        if (containsTimeoutFor(driver)) {
            return Optional.of(timeouts.get(driver).peek());
        } else {
            return Optional.absent();
        }
    }

    public void releaseTimeoutFor(WebDriver driverInstance) {
        timeouts.remove(driverInstance);
    }
}

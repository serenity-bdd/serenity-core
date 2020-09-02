package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * Created by john on 12/03/15.
 */
public class TimeoutStack {

    Map<WebDriver, Stack<Duration>> timeouts = new HashMap();

    public void pushTimeoutFor(WebDriver driver, Duration implicitTimeout) {
        if (!timeouts.containsKey(driver)) {
            timeouts.put(driver, new Stack<>());
        }
        timeouts.get(driver).push(implicitTimeout);
    }

    public Optional<Duration> popTimeoutFor(WebDriver driver) {
        if (timeouts.containsKey(driver)) {
            return timeouts.get(driver).isEmpty() ? Optional.<Duration>empty() :
                                                           Optional.of(timeouts.get(driver).pop());
        } else {
            return Optional.empty();
        }
    }

    public Boolean containsTimeoutFor(WebDriver driver) {
        return timeouts.containsKey(driver) && !timeouts.get(driver).isEmpty();
    }

    public Optional<Duration> currentTimeoutValueFor(WebDriver driver) {
        if (containsTimeoutFor(driver)) {
            return Optional.of(timeouts.get(driver).peek());
        } else {
            return Optional.empty();
        }
    }

    public void releaseTimeoutFor(WebDriver driverInstance) {
        timeouts.remove(driverInstance);
    }
}

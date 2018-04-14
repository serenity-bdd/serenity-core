package sample.steps;

import org.openqa.selenium.ElementNotVisibleException;

/**
 * Created by john on 4/07/2014.
 */
public class FailingStep {

    public Exception failsWithMessage(String message) {
        return new IllegalArgumentException(message);
    }

    public Exception failsWith(ElementNotVisibleException cause) {
        return cause;
    }
}

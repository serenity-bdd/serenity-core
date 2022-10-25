package net.serenitybdd.core.pages;

import org.openqa.selenium.InvalidElementStateException;

import java.time.Duration;
import java.util.function.Consumer;

public class WithRetries {
    private final WebElementFacadeImpl elementFacade;

    public static WithRetries on(WebElementFacadeImpl elementFacade) {
        return new WithRetries(elementFacade);
    }

    private WithRetries(WebElementFacadeImpl elementFacade) {
        this.elementFacade = elementFacade;
    }

    public void perform(Consumer<WebElementFacadeImpl> action, int remainingTries) {
        try {
            if (remainingTries > 0) {
                action.accept(elementFacade);
            }
        } catch (InvalidElementStateException couldNotInteractWithElement) {
            sleep(Duration.ofMillis(250));
            if (remainingTries > 1) {
                perform(action, remainingTries - 1);
            } else {
                throw couldNotInteractWithElement;
            }
        }
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

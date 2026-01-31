package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.function.Consumer;

/**
 * Perform actions within an iframe.
 *
 * <p>Example usage:</p>
 * <pre>
 * actor.attemptsTo(
 *     WithinFrame.locatedBy("#my-iframe")
 *         .click("#button-inside-frame")
 * );
 *
 * // Or for complex operations:
 * actor.attemptsTo(
 *     WithinFrame.locatedBy("#my-iframe")
 *         .perform(frame -> {
 *             frame.locator("#username").fill("user");
 *             frame.locator("#password").fill("pass");
 *             frame.locator("#submit").click();
 *         })
 * );
 * </pre>
 */
public class WithinFrame {

    private final String frameSelector;

    private WithinFrame(String frameSelector) {
        this.frameSelector = frameSelector;
    }

    /**
     * Locate an iframe by its selector.
     */
    public static WithinFrame locatedBy(String frameSelector) {
        return new WithinFrame(frameSelector);
    }

    /**
     * Click on an element within the frame.
     */
    public Performable click(String selector) {
        return new FrameClick(frameSelector, selector);
    }

    /**
     * Fill a value into an input within the frame.
     */
    public Performable fill(String selector, String value) {
        return new FrameFill(frameSelector, selector, value);
    }

    /**
     * Clear an input within the frame.
     */
    public Performable clear(String selector) {
        return new FrameClear(frameSelector, selector);
    }

    /**
     * Get text from an element within the frame.
     */
    public Performable type(String selector, String text) {
        return new FrameType(frameSelector, selector, text);
    }

    /**
     * Perform custom actions within the frame using the FrameLocator.
     */
    public Performable perform(Consumer<FrameLocator> actions) {
        return new FrameCustomAction(frameSelector, actions);
    }
}

class FrameClick implements Performable {
    private final String frameSelector;
    private final String elementSelector;

    FrameClick(String frameSelector, String elementSelector) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
    }

    @Override
    @Step("{0} clicks on #elementSelector within frame #frameSelector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        frame.locator(elementSelector).click();
        ability.notifyScreenChange();
    }
}

class FrameFill implements Performable {
    private final String frameSelector;
    private final String elementSelector;
    private final String value;

    FrameFill(String frameSelector, String elementSelector, String value) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
        this.value = value;
    }

    @Override
    @Step("{0} fills '#value' into #elementSelector within frame #frameSelector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        frame.locator(elementSelector).fill(value);
        ability.notifyScreenChange();
    }
}

class FrameClear implements Performable {
    private final String frameSelector;
    private final String elementSelector;

    FrameClear(String frameSelector, String elementSelector) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
    }

    @Override
    @Step("{0} clears #elementSelector within frame #frameSelector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        frame.locator(elementSelector).clear();
        ability.notifyScreenChange();
    }
}

class FrameType implements Performable {
    private final String frameSelector;
    private final String elementSelector;
    private final String text;

    FrameType(String frameSelector, String elementSelector, String text) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
        this.text = text;
    }

    @Override
    @Step("{0} types '#text' into #elementSelector within frame #frameSelector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        frame.locator(elementSelector).pressSequentially(text);
        ability.notifyScreenChange();
    }
}

class FrameCustomAction implements Performable {
    private final String frameSelector;
    private final Consumer<FrameLocator> actions;

    FrameCustomAction(String frameSelector, Consumer<FrameLocator> actions) {
        this.frameSelector = frameSelector;
        this.actions = actions;
    }

    @Override
    @Step("{0} performs actions within frame #frameSelector")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page page = ability.getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        actions.accept(frame);
        ability.notifyScreenChange();
    }
}

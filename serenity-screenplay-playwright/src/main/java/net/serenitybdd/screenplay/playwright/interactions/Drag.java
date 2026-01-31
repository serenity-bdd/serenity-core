package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Drag an element to another element (drag and drop).
 *
 * <p>Sample usage:</p>
 * <pre>
 *     actor.attemptsTo(
 *         Drag.from("#source").to("#target"),
 *         Drag.from(SOURCE_ELEMENT).to(TARGET_ELEMENT)
 *     );
 * </pre>
 */
public class Drag {

    private final Target source;

    private Drag(Target source) {
        this.source = source;
    }

    /**
     * Start a drag operation from the specified selector.
     */
    public static Drag from(String selector) {
        return new Drag(Target.the(selector).locatedBy(selector));
    }

    /**
     * Start a drag operation from the specified Target.
     */
    public static Drag from(Target source) {
        return new Drag(source);
    }

    /**
     * Complete the drag operation by dropping onto the specified selector.
     */
    public Performable to(String selector) {
        return to(Target.the(selector).locatedBy(selector));
    }

    /**
     * Complete the drag operation by dropping onto the specified Target.
     */
    public Performable to(Target destination) {
        return new DragTo(source, destination);
    }

    private static class DragTo implements Performable {
        private final Target source;
        private final Target destination;
        private Locator.DragToOptions options;

        DragTo(Target source, Target destination) {
            this.source = source;
            this.destination = destination;
        }

        public DragTo withOptions(Locator.DragToOptions options) {
            this.options = options;
            return this;
        }

        @Override
        @Step("{0} drags #source to #destination")
        public <T extends Actor> void performAs(T actor) {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
            Locator sourceLocator = source.resolveFor(page);
            Locator destLocator = destination.resolveFor(page);
            sourceLocator.dragTo(destLocator, options);
            BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
        }
    }
}

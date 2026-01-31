package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Get the bounding box (position and size) of an element.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     BoundingBox box = actor.asksFor(ElementBounds.of("#element"));
 *     double width = box.getWidth();
 *     double height = box.getHeight();
 * </pre>
 */
public class ElementBounds implements Question<BoundingBox> {

    private final Target target;

    private ElementBounds(Target target) {
        this.target = target;
    }

    /**
     * Get bounds for an element identified by a selector.
     */
    public static ElementBounds of(String selector) {
        return new ElementBounds(Target.the(selector).locatedBy(selector));
    }

    /**
     * Get bounds for a Target element.
     */
    public static ElementBounds of(Target target) {
        return new ElementBounds(target);
    }

    @Override
    public BoundingBox answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator locator = target.resolveFor(page);
        com.microsoft.playwright.options.BoundingBox box = locator.boundingBox();
        if (box == null) {
            return null;
        }
        return new BoundingBox(box.x, box.y, box.width, box.height);
    }

    @Override
    public String toString() {
        return "bounding box of " + target;
    }
}

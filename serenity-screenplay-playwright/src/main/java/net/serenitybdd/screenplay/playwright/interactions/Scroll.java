package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Comprehensive scrolling operations for both page-level and element scrolling.
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Page-level scrolling
 *     actor.attemptsTo(Scroll.toTop());
 *     actor.attemptsTo(Scroll.toBottom());
 *     actor.attemptsTo(Scroll.by(0, 500));
 *     actor.attemptsTo(Scroll.toPosition(0, 1000));
 *
 *     // Element scrolling with alignment
 *     actor.attemptsTo(Scroll.to("#footer"));
 *     actor.attemptsTo(Scroll.to(FOOTER).andAlignToCenter());
 *     actor.attemptsTo(Scroll.to(ELEMENT).andAlignToTop());
 * </pre>
 */
public class Scroll {

    private Scroll() {
        // Factory class - prevent instantiation
    }

    /**
     * Scroll to an element identified by a selector.
     *
     * @param selector The CSS or XPath selector
     * @return A ScrollToElement interaction for fluent configuration
     */
    public static ScrollToElement to(String selector) {
        return new ScrollToElement(Target.the(selector).locatedBy(selector));
    }

    /**
     * Scroll to a Target element.
     *
     * @param target The target element
     * @return A ScrollToElement interaction for fluent configuration
     */
    public static ScrollToElement to(Target target) {
        return new ScrollToElement(target);
    }

    /**
     * Scroll to the top of the page.
     *
     * @return A Performable that scrolls to the top
     */
    public static Performable toTop() {
        return new ScrollToTop();
    }

    /**
     * Scroll to the bottom of the page.
     *
     * @return A Performable that scrolls to the bottom
     */
    public static Performable toBottom() {
        return new ScrollToBottom();
    }

    /**
     * Scroll by the specified delta amounts.
     *
     * @param deltaX Horizontal scroll amount (positive = right)
     * @param deltaY Vertical scroll amount (positive = down)
     * @return A Performable that scrolls by the specified amounts
     */
    public static Performable by(int deltaX, int deltaY) {
        return new ScrollBy(deltaX, deltaY);
    }

    /**
     * Scroll to an absolute position on the page.
     *
     * @param x The horizontal position
     * @param y The vertical position
     * @return A Performable that scrolls to the specified position
     */
    public static Performable toPosition(int x, int y) {
        return new ScrollToPosition(x, y);
    }

    /**
     * Element scrolling with alignment options.
     */
    public static class ScrollToElement implements Performable {
        private final Target target;
        private String alignment = "center";

        ScrollToElement(Target target) {
            this.target = target;
        }

        /**
         * Align the element to the top of the viewport after scrolling.
         */
        public ScrollToElement andAlignToTop() {
            this.alignment = "start";
            return this;
        }

        /**
         * Align the element to the center of the viewport after scrolling.
         */
        public ScrollToElement andAlignToCenter() {
            this.alignment = "center";
            return this;
        }

        /**
         * Align the element to the bottom of the viewport after scrolling.
         */
        public ScrollToElement andAlignToBottom() {
            this.alignment = "end";
            return this;
        }

        @Override
        @Step("{0} scrolls to #target")
        public <T extends Actor> void performAs(T actor) {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
            String script = "el => el.scrollIntoView({block: '" + alignment + "', behavior: 'instant'})";
            target.resolveFor(page).evaluate(script);
            BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
        }
    }

    private static class ScrollToTop implements Performable {
        @Override
        @Step("{0} scrolls to the top of the page")
        public <T extends Actor> void performAs(T actor) {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
            page.evaluate("window.scrollTo(0, 0)");
            BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
        }
    }

    private static class ScrollToBottom implements Performable {
        @Override
        @Step("{0} scrolls to the bottom of the page")
        public <T extends Actor> void performAs(T actor) {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
            BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
        }
    }

    private static class ScrollBy implements Performable {
        private final int deltaX;
        private final int deltaY;

        ScrollBy(int deltaX, int deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }

        @Override
        @Step("{0} scrolls by ({1}, {2})")
        public <T extends Actor> void performAs(T actor) {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
            page.mouse().wheel(deltaX, deltaY);
            BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
        }
    }

    private static class ScrollToPosition implements Performable {
        private final int x;
        private final int y;

        ScrollToPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        @Step("{0} scrolls to position ({1}, {2})")
        public <T extends Actor> void performAs(T actor) {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
            page.evaluate("window.scrollTo(" + x + ", " + y + ")");
            BrowseTheWebWithPlaywright.as(actor).notifyScreenChange();
        }
    }
}

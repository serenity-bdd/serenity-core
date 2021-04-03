package net.serenitybdd.screenplay.playwright;

/**
 * Locate an element on the page using a Playwright selector.
 * Playwright selectors can use text, css or even xpath to locate an element.
 *
 * See also: https://playwright.dev/java/docs/selectors/
 */
public class Target {
    private final String label;
    private final String selector;

    public Target(String label, String selector) {
        this.label = label;
        this.selector = selector;
    }

    public static TargetBuilder the(String label) {
        return new TargetBuilder(label);
    }

    public static class TargetBuilder {

        private final String label;

        public TargetBuilder(String label) {
            this.label = label;
        }

        public Target locatedBy(String selector) {
            return new Target(label, selector);
        }
    }

    public String asSelector() {
        return selector;
    }

    @Override
    public String toString() {
        return label;
    }
}

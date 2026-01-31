package net.serenitybdd.screenplay.playwright;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Locate an element on the page using a Playwright selector.
 * Playwright selectors can use text, css, xpath, role selectors, and more.
 *
 * <p>Supports:</p>
 * <ul>
 *   <li>Parameterized selectors: {@code Target.the("item {0}").locatedBy("[data-id='{0}']").of("123")}</li>
 *   <li>Nested targets: {@code button.inside(form)}</li>
 *   <li>Chained locators: {@code form.find(button)}</li>
 *   <li>Frame support: {@code Target.the("editor").inFrame("#iframe").locatedBy("#content")}</li>
 * </ul>
 *
 * @see <a href="https://playwright.dev/java/docs/selectors/">Playwright Selectors</a>
 */
public class Target {

    private final String label;
    private final String selector;
    private final List<String> framePath;
    private final Target container;

    /**
     * Create a new Target with label and selector.
     */
    public Target(String label, String selector) {
        this(label, selector, Collections.emptyList(), null);
    }

    /**
     * Create a new Target with all properties.
     */
    protected Target(String label, String selector, List<String> framePath, Target container) {
        this.label = label;
        this.selector = selector;
        this.framePath = new ArrayList<>(framePath);
        this.container = container;
    }

    /**
     * Start building a Target with a descriptive label.
     *
     * @param label Human-readable description of the element
     * @return A TargetBuilder to continue configuration
     */
    public static TargetBuilder the(String label) {
        return new TargetBuilder(label);
    }

    /**
     * Builder for creating Target instances.
     */
    public static class TargetBuilder {
        private final String label;
        private final List<String> framePath;

        public TargetBuilder(String label) {
            this.label = label;
            this.framePath = new ArrayList<>();
        }

        private TargetBuilder(String label, List<String> framePath) {
            this.label = label;
            this.framePath = new ArrayList<>(framePath);
        }

        /**
         * Specify that this target is inside an iframe.
         * Can be called multiple times for nested frames.
         *
         * @param frameSelector Selector for the iframe element
         * @return This builder for chaining
         */
        public TargetBuilder inFrame(String frameSelector) {
            this.framePath.add(frameSelector);
            return new TargetBuilder(label, framePath);
        }

        /**
         * Specify the selector for the target element.
         *
         * @param selector Playwright selector (CSS, XPath, text, role, etc.)
         * @return The configured Target
         */
        public Target locatedBy(String selector) {
            return new Target(label, selector, framePath, null);
        }
    }

    /**
     * Create a parameterized version of this target by substituting placeholders.
     * Placeholders use the format {0}, {1}, etc.
     *
     * <p>Example:</p>
     * <pre>
     * Target ITEM = Target.the("item {0}").locatedBy("[data-id='{0}']");
     * Target item123 = ITEM.of("123"); // selector becomes "[data-id='123']"
     * </pre>
     *
     * @param parameters Values to substitute for {0}, {1}, etc.
     * @return A new Target with substituted values (original is unchanged)
     */
    public Target of(String... parameters) {
        String resolvedLabel = substituteParameters(label, parameters);
        String resolvedSelector = substituteParameters(selector, parameters);
        return new Target(resolvedLabel, resolvedSelector, framePath, container);
    }

    /**
     * Create a target that locates this element inside a container element.
     * Uses Playwright's locator chaining: {@code container.locator(this)}
     *
     * <p>Example:</p>
     * <pre>
     * Target BUTTON = Target.the("submit button").locatedBy("button[type='submit']");
     * Target FORM = Target.the("login form").locatedBy("#login-form");
     * Target submitInForm = BUTTON.inside(FORM);
     * </pre>
     *
     * @param containerTarget The container element
     * @return A new Target that searches within the container
     */
    public Target inside(Target containerTarget) {
        String combinedLabel = label + " inside " + containerTarget.label;
        return new Target(combinedLabel, selector, framePath, containerTarget);
    }

    /**
     * Create a target that chains this locator with a child locator.
     * Uses Playwright's locator chaining: {@code this.locator(child)}
     *
     * <p>Example:</p>
     * <pre>
     * Target FORM = Target.the("login form").locatedBy("#login-form");
     * Target BUTTON = Target.the("submit button").locatedBy("button[type='submit']");
     * Target formButton = FORM.find(BUTTON);
     * </pre>
     *
     * @param childTarget The child element to find within this target
     * @return A new Target with chained selectors
     */
    public Target find(Target childTarget) {
        String combinedLabel = childTarget.label + " in " + label;
        return new Target(combinedLabel, childTarget.selector, framePath, this);
    }

    /**
     * Specify that this target is inside an iframe.
     * Can be called multiple times for nested frames.
     *
     * @param frameSelector Selector for the iframe element
     * @return A new Target with the frame path
     */
    public Target inFrame(String frameSelector) {
        List<String> newFramePath = new ArrayList<>(framePath);
        newFramePath.add(frameSelector);
        return new Target(label, selector, newFramePath, container);
    }

    /**
     * Get the raw selector string.
     *
     * @return The Playwright selector
     */
    public String asSelector() {
        if (container != null) {
            return container.asSelector() + " >> " + selector;
        }
        return selector;
    }

    /**
     * Check if this target is inside an iframe.
     *
     * @return true if the target requires frame navigation
     */
    public boolean isInFrame() {
        return !framePath.isEmpty();
    }

    /**
     * Get the frame path for this target.
     *
     * @return List of frame selectors (empty if not in a frame)
     */
    public List<String> getFramePath() {
        return Collections.unmodifiableList(framePath);
    }

    /**
     * Resolve this target to a Playwright Locator.
     *
     * @param page The Playwright Page
     * @return A Locator for this target
     */
    public Locator resolveFor(Page page) {
        // Handle frame navigation
        if (!framePath.isEmpty()) {
            FrameLocator frameLocator = page.frameLocator(framePath.get(0));
            for (int i = 1; i < framePath.size(); i++) {
                frameLocator = frameLocator.frameLocator(framePath.get(i));
            }
            return resolveWithinFrame(frameLocator);
        }

        // Handle container chaining
        if (container != null) {
            Locator containerLocator = container.resolveFor(page);
            return containerLocator.locator(selector);
        }

        return page.locator(selector);
    }

    /**
     * Resolve this target within a frame locator.
     */
    private Locator resolveWithinFrame(FrameLocator frameLocator) {
        if (container != null) {
            // For nested containers within frames, we need to chain locators
            Locator containerLocator = frameLocator.locator(container.selector);
            return containerLocator.locator(selector);
        }
        return frameLocator.locator(selector);
    }

    /**
     * Get the container target (if any).
     *
     * @return The container target, or null if not nested
     */
    public Target getContainer() {
        return container;
    }

    @Override
    public String toString() {
        return label;
    }

    /**
     * Substitute parameters into a string.
     */
    private String substituteParameters(String template, String... parameters) {
        String result = template;
        for (int i = 0; i < parameters.length; i++) {
            result = result.replace("{" + i + "}", parameters[i]);
        }
        return result;
    }
}

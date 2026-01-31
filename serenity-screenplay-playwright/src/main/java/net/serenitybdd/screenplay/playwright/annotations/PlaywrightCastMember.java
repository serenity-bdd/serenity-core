package net.serenitybdd.screenplay.playwright.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to inject an Actor with the BrowseTheWebWithPlaywright ability into a test.
 * The actor will be configured with a Playwright browser based on the annotation attributes.
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @ExtendWith(SerenityJUnit5Extension.class)
 * public class MyPlaywrightTest {
 *
 *     @PlaywrightCastMember(name = "Alice")
 *     Actor alice;
 *
 *     @PlaywrightCastMember(browserType = "firefox", headless = false)
 *     Actor bob;
 *
 *     @Test
 *     void should_browse_with_playwright() {
 *         alice.attemptsTo(
 *             Open.url("https://example.com"),
 *             Ensure.that("h1").isVisible()
 *         );
 *     }
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PlaywrightCastMember {

    /**
     * The name of the actor. If not specified, the capitalized field name will be used.
     */
    String name() default "";

    /**
     * A description of the actor, which will appear alongside the actor name in reports.
     */
    String description() default "";

    /**
     * The browser type to use. Supported values are "chromium", "firefox", and "webkit".
     * Default is "chromium".
     */
    String browserType() default "chromium";

    /**
     * Whether to run the browser in headless mode. Default is true.
     */
    boolean headless() default true;
}
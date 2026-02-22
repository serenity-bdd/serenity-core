package net.serenitybdd.playwright.junit5;

import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convenience meta-annotation that combines Serenity and Playwright extensions.
 * <p>
 * This annotation provides a shorthand for configuring a Serenity BDD test with
 * Playwright support. Using this annotation is equivalent to adding both
 * {@code @ExtendWith(SerenityJUnit5Extension.class)} and
 * {@code @ExtendWith(SerenityPlaywrightExtension.class)}.
 * </p>
 * <p>
 * Basic usage:
 * <pre>
 * &#64;SerenityPlaywright
 * class MyPlaywrightTest {
 *
 *     &#64;Steps
 *     MySteps steps;
 *
 *     private Playwright playwright;
 *     private Browser browser;
 *     private Page page;
 *
 *     &#64;BeforeEach
 *     void setup() {
 *         playwright = Playwright.create();
 *         browser = playwright.chromium().launch();
 *         page = browser.newPage();
 *         PlaywrightSerenity.registerPage(page);
 *     }
 *
 *     &#64;AfterEach
 *     void teardown() {
 *         PlaywrightSerenity.clear();
 *         browser.close();
 *         playwright.close();
 *     }
 *
 *     &#64;Test
 *     void canSearchForProducts() {
 *         steps.openHomePage();
 *         steps.searchFor("laptop");
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * For use with Playwright's {@code @UsePlaywright} annotation, Playwright manages the
 * full browser lifecycle and Page instances are automatically registered with Serenity:
 * <pre>
 * &#64;SerenityPlaywright
 * &#64;UsePlaywright(ChromeHeadlessOptions.class)
 * class MyPlaywrightTest {
 *
 *     &#64;Steps
 *     MySteps steps;
 *
 *     &#64;BeforeEach
 *     void setUp(Page page) {
 *         // Page is created by Playwright and auto-registered with Serenity
 *     }
 *
 *     &#64;Test
 *     void canSearchForProducts(Page page) {
 *         page.navigate("https://example.com");
 *         steps.verifyPageLoaded(page);
 *     }
 * }
 * </pre>
 * </p>
 *
 * @see SerenityPlaywrightExtension
 * @see SerenityJUnit5Extension
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SerenityJUnit5Extension.class)
@ExtendWith(SerenityPlaywrightExtension.class)
public @interface SerenityPlaywright {
}

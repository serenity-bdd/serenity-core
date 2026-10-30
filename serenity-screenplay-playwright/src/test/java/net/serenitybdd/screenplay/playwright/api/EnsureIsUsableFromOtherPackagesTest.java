package net.serenitybdd.screenplay.playwright.api;

import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.assertions.Ensure;
import net.serenitybdd.screenplay.playwright.assertions.PageTitleEnsure;
import net.serenitybdd.screenplay.playwright.assertions.UrlEnsure;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ensure.thatTheCurrentUrl() and Ensure.thatThePageTitle() must return types that user code (outside the
 * assertions package) can call methods on. This test lives in a different package on purpose: it fails to
 * compile if the returned classes are not public.
 */
class EnsureIsUsableFromOtherPackagesTest {

    @Test
    void urlAssertionsCanBeUsedFromUserCode() {
        UrlEnsure url = Ensure.thatTheCurrentUrl().withTimeout(1000.0);
        Performable contains = url.contains("/todo");
        Performable equalTo = url.isEqualTo("https://example.org/todo");
        Performable matches = url.matches(Pattern.compile(".*/todo"));
        assertThat(contains).isNotNull();
        assertThat(equalTo).isNotNull();
        assertThat(matches).isNotNull();
    }

    @Test
    void pageTitleAssertionsCanBeUsedFromUserCode() {
        PageTitleEnsure title = Ensure.thatThePageTitle().withTimeout(1000.0);
        Performable contains = title.contains("Todo");
        Performable equalTo = title.isEqualTo("Todo list");
        Performable matches = title.matches(Pattern.compile("Todo.*"));
        assertThat(contains).isNotNull();
        assertThat(equalTo).isNotNull();
        assertThat(matches).isNotNull();
    }
}

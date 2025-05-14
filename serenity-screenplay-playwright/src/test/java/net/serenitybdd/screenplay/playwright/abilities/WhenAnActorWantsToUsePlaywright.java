package net.serenitybdd.screenplay.playwright.abilities;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.BrowserType;
import net.serenitybdd.screenplay.Actor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("When an actor wants to use the Playwright library")
class WhenAnActorWantsToUsePlaywright {

    @DisplayName("We assign them the Playwright ability")
    @Nested
    class AssignPlaywrightAbility {

        Actor william = Actor.named("William");

        @DisplayName("We can opt for default configuration values")
        @Test
        void usingTheDefaultConfiguration() {

            william.can(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

            Page page = BrowseTheWebWithPlaywright.as(william).getBrowser().newPage();

            page.navigate("https://duckduckgo.com/");

            assertThat(page.title()).contains("DuckDuckGo");
        }

        @DisplayName("We can define options for the Playwright session")
        @Test
        void usingPlaywrightOptions() {

            william.can(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration());

            Page page = BrowseTheWebWithPlaywright.as(william).getBrowser().newPage();

            page.navigate("https://duckduckgo.com/");

            assertThat(page.title()).contains("DuckDuckGo");
        }

        @DisplayName("We can use a different browser for the Playwright session")
        @Test
        void usingADifferentBrowser() {

            william.can(BrowseTheWebWithPlaywright.usingTheDefaultConfiguration().withBrowserType("firefox"));

            Page page = BrowseTheWebWithPlaywright.as(william).getBrowser().newPage();

            page.navigate("https://duckduckgo.com/");

            assertThat(page.title()).contains("DuckDuckGo");
        }

        @DisplayName("if we get the driver type wrong we get a meaningful message")
        @Test
        void throwExceptionWhenTheWrongDriverIsUsed() {

            BrowseTheWebWithPlaywright browseTheWebWithPlaywright
                    = BrowseTheWebWithPlaywright.usingTheDefaultConfiguration().withBrowserType("no-such-browser");

            Exception exception = assertThrows(
                    InvalidPlaywrightBrowserType.class,
                    browseTheWebWithPlaywright::getBrowser);

            assertThat(exception.getMessage()).contains("Invalid Playwright browser type: no-such-browser; Must be one of chromium, webkit or firefox");
        }

        @DisplayName("if we forget to assign the ability we get a meaningful message")
        @Test
        void throwExceptionWhenNoAbilityIsAssigned() {
            Actor billy = Actor.named("Billy");

            Exception exception = assertThrows(
                    ActorCannotUsePlaywrightException.class,
                    () -> BrowseTheWebWithPlaywright.as(billy));

            assertThat(exception.getMessage()).contains("The actor Billy does not have the ability to use Playwright");
        }

        @DisplayName("We can use the 'chromium' channel for the Playwright session")
        @Test
        void usingTheChromiumChannel() {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setChannel("chromium");
            william.can(BrowseTheWebWithPlaywright.withOptions(launchOptions).withBrowserType("chromium"));

            Page page = BrowseTheWebWithPlaywright.as(william).getBrowser().newPage();

            page.navigate("https://duckduckgo.com/");

            assertThat(page.title()).contains("DuckDuckGo");
        }

    }
}

package net.serenitybdd.screenplay.playwright.actors;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlaywrightCast")
class PlaywrightCastTest {

    @Nested
    @DisplayName("When withOptions(OptionsFactory) is configured")
    class WithOptionsFactory {

        @Test
        @DisplayName("should give each actor a BrowseTheWebWithPlaywright ability")
        void shouldGiveEachActorPlaywrightAbility() {
            OptionsFactory factory = () -> new Options().setBrowserName("chromium");
            PlaywrightCast cast = new PlaywrightCast().withOptions(factory);

            Actor actor = cast.actorNamed("Alice");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
        }

        @Test
        @DisplayName("should return the cast instance for fluent chaining")
        void shouldReturnCastForFluentChaining() {
            OptionsFactory factory = Options::new;
            PlaywrightCast cast = new PlaywrightCast();

            PlaywrightCast result = cast.withOptions(factory);

            assertThat(result).isSameAs(cast);
        }

        @Test
        @DisplayName("should give different actors independent ability instances")
        void shouldGiveDifferentActorsIndependentAbilities() {
            OptionsFactory factory = Options::new;
            PlaywrightCast cast = new PlaywrightCast().withOptions(factory);

            Actor alice = cast.actorNamed("Alice");
            Actor bob = cast.actorNamed("Bob");

            BrowseTheWebWithPlaywright aliceAbility = alice.abilityTo(BrowseTheWebWithPlaywright.class);
            BrowseTheWebWithPlaywright bobAbility = bob.abilityTo(BrowseTheWebWithPlaywright.class);
            assertThat(aliceAbility).isNotSameAs(bobAbility);
        }

        @Test
        @DisplayName("should not add a second ability when actor already has one")
        void shouldNotAddSecondAbilityWhenActorAlreadyHasOne() {
            OptionsFactory factory = Options::new;
            PlaywrightCast cast = new PlaywrightCast().withOptions(factory);

            Actor actor = cast.actorNamed("Eve");
            BrowseTheWebWithPlaywright firstAbility = actor.abilityTo(BrowseTheWebWithPlaywright.class);

            // Calling actorNamed again returns the same cached actor
            Actor sameActor = cast.actorNamed("Eve");
            BrowseTheWebWithPlaywright secondAbility = sameActor.abilityTo(BrowseTheWebWithPlaywright.class);

            assertThat(secondAbility).isSameAs(firstAbility);
        }
    }

    @Nested
    @DisplayName("When no OptionsFactory is set")
    class WithoutOptionsFactory {

        @Test
        @DisplayName("should provide default ability when no options configured")
        void shouldProvideDefaultAbility() {
            PlaywrightCast cast = new PlaywrightCast();

            Actor actor = cast.actorNamed("Charlie");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
        }

        @Test
        @DisplayName("should apply launchOptions when set")
        void shouldApplyLaunchOptions() {
            BrowserType.LaunchOptions opts = new BrowserType.LaunchOptions().setHeadless(true);
            PlaywrightCast cast = new PlaywrightCast().withLaunchOptions(opts);

            Actor actor = cast.actorNamed("Dave");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
        }

        @Test
        @DisplayName("should apply contextOptions when set")
        void shouldApplyContextOptions() {
            Browser.NewContextOptions ctxOpts = new Browser.NewContextOptions()
                    .setViewportSize(1920, 1080);
            PlaywrightCast cast = new PlaywrightCast().withContextOptions(ctxOpts);

            Actor actor = cast.actorNamed("Frank");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
        }

        @Test
        @DisplayName("should apply browserType when set")
        void shouldApplyBrowserType() {
            PlaywrightCast cast = new PlaywrightCast().withBrowserType("firefox");

            Actor actor = cast.actorNamed("Grace");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
        }

        @Test
        @DisplayName("should apply headless mode when set")
        void shouldApplyHeadlessMode() {
            PlaywrightCast cast = new PlaywrightCast().withHeadlessMode(true);

            Actor actor = cast.actorNamed("Heidi");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
        }
    }

    @Nested
    @DisplayName("Static factory methods")
    class StaticFactoryMethods {

        @Test
        @DisplayName("whereEveryoneCan with abilities should provide playwright plus extra abilities")
        void whereEveryoneCanWithAbilities() {
            PlaywrightCast cast = PlaywrightCast.whereEveryoneCan(new net.serenitybdd.screenplay.Ability[]{});

            Actor actor = cast.actorNamed("Ivan");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
        }

        @Test
        @DisplayName("whereEveryoneCan with providers should configure each actor")
        void whereEveryoneCanWithProviders() {
            PlaywrightCast cast = PlaywrightCast.whereEveryoneCan(
                    actor -> actor.remember("configured", true)
            );

            Actor actor = cast.actorNamed("Judy");

            assertThat(actor.abilityTo(BrowseTheWebWithPlaywright.class)).isNotNull();
            assertThat((Boolean) actor.recall("configured")).isTrue();
        }
    }
}

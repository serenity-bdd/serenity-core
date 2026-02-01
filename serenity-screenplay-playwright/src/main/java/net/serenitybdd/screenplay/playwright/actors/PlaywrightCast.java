package net.serenitybdd.screenplay.playwright.actors;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.function.Consumer;

/**
 * A Cast implementation that automatically provides actors with the ability to browse the web using Playwright.
 * <p>
 * This class simplifies Screenplay tests by managing actor lifecycle and Playwright browser instances.
 * Actors created through this Cast will automatically have the BrowseTheWebWithPlaywright ability.
 * <p>
 * <h2>Usage with JUnit 5:</h2>
 * <pre>{@code
 * @BeforeEach
 * void setupStage() {
 *     OnStage.setTheStage(new PlaywrightCast());
 * }
 *
 * @AfterEach
 * void teardownStage() {
 *     OnStage.drawTheCurtain();
 * }
 *
 * @Test
 * void shouldSearchForProducts() {
 *     Actor customer = OnStage.theActorCalled("Customer");
 *     customer.attemptsTo(
 *         Open.url("https://example.com"),
 *         SearchFor.product("Widget")
 *     );
 * }
 * }</pre>
 * <p>
 * <h2>Usage with Cucumber:</h2>
 * <p>
 * Create a hooks class in your step definitions package:
 * <pre>{@code
 * public class PlaywrightHooks {
 *
 *     @Before(order = 0)
 *     public void setTheStage(Scenario scenario) {
 *         OnStage.setTheStage(new PlaywrightCast());
 *     }
 *
 *     @ParameterType(".*")
 *     public Actor actor(String actorName) {
 *         return OnStage.theActorCalled(actorName);
 *     }
 * }
 * }</pre>
 * <p>
 * The cleanup is handled automatically by Serenity's StageDirector when you include
 * {@code net.serenitybdd.cucumber.actors} in your Cucumber glue path:
 * <pre>{@code
 * @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "your.steps,net.serenitybdd.cucumber.actors")
 * }</pre>
 * <p>
 * The OnStage.drawTheCurtain() call will automatically clean up all browser resources.
 */
public class PlaywrightCast extends Cast {

    private BrowserType.LaunchOptions launchOptions;
    private Browser.NewContextOptions contextOptions;
    private String browserType;

    public PlaywrightCast() {
        super(new Ability[]{});
    }

    public PlaywrightCast(Ability[] abilities) {
        super(abilities);
    }

    @SafeVarargs
    public PlaywrightCast(Consumer<Actor>... providers) {
        super(providers);
    }

    /**
     * Create a PlaywrightCast where all actors have the specified abilities in addition to BrowseTheWebWithPlaywright.
     */
    public static PlaywrightCast whereEveryoneCan(Ability... abilities) {
        return new PlaywrightCast(abilities);
    }

    /**
     * Create a PlaywrightCast where each actor is configured using the provided function.
     */
    @SafeVarargs
    public static PlaywrightCast whereEveryoneCan(Consumer<Actor>... abilityProviders) {
        return new PlaywrightCast(abilityProviders);
    }

    /**
     * Configure launch options for the Playwright browser.
     */
    public PlaywrightCast withLaunchOptions(BrowserType.LaunchOptions options) {
        this.launchOptions = options;
        return this;
    }

    /**
     * Configure context options for browser contexts.
     */
    public PlaywrightCast withContextOptions(Browser.NewContextOptions options) {
        this.contextOptions = options;
        return this;
    }

    /**
     * Set the browser type (chromium, firefox, or webkit).
     */
    public PlaywrightCast withBrowserType(String browserType) {
        this.browserType = browserType;
        return this;
    }

    /**
     * Set headless mode for the browser.
     */
    public PlaywrightCast withHeadlessMode(boolean headless) {
        if (this.launchOptions == null) {
            this.launchOptions = new BrowserType.LaunchOptions();
        }
        this.launchOptions.setHeadless(headless);
        return this;
    }

    @Override
    public Actor actorNamed(String actorName, Ability... abilities) {
        Actor newActor = super.actorNamed(actorName, abilities);

        // Add BrowseTheWebWithPlaywright if not already present
        if (newActor.abilityTo(BrowseTheWebWithPlaywright.class) == null) {
            newActor.can(createPlaywrightAbility());
        }

        return newActor;
    }

    private BrowseTheWebWithPlaywright createPlaywrightAbility() {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.usingTheDefaultConfiguration();

        if (launchOptions != null) {
            ability = BrowseTheWebWithPlaywright.withOptions(launchOptions);
        }

        if (contextOptions != null) {
            ability = ability.withContextOptions(contextOptions);
        }

        if (browserType != null) {
            ability = ability.withBrowserType(browserType);
        }

        return ability;
    }
}

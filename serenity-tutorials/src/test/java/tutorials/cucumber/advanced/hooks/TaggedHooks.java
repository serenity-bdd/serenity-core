package tutorials.cucumber.advanced.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

/**
 * Demonstrates tagged hooks that only run for specific scenarios.
 */
public class TaggedHooks {

    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";

    /**
     * Runs only for scenarios tagged with @api.
     * Gives all actors the ability to call APIs.
     */
    @Before("@api")
    public void setupApiAbility() {
        System.out.println("[TAGGED HOOKS] Setting up API ability for @api scenario");
        // This would typically configure API-specific settings
    }

    /**
     * Runs only for scenarios tagged with @authenticated.
     * Sets up authentication before the scenario.
     */
    @Before("@authenticated")
    public void setupAuthentication() {
        System.out.println("[TAGGED HOOKS] Setting up authentication for @authenticated scenario");
        // Would perform login or set auth tokens here
    }

    /**
     * Runs for scenarios tagged with @slow.
     * Could increase timeouts or configure patience settings.
     */
    @Before("@slow")
    public void configureForSlowTests() {
        System.out.println("[TAGGED HOOKS] Configuring for slow test");
        // Would adjust timeouts here
    }

    /**
     * Cleanup that only runs for @api scenarios.
     */
    @After("@api")
    public void cleanupAfterApi(Scenario scenario) {
        System.out.println("[TAGGED HOOKS] Cleaning up after @api scenario: " + scenario.getName());
    }

    /**
     * Complex tag expression: runs for @smoke OR @regression, but NOT @wip.
     */
    @Before("(@smoke or @regression) and not @wip")
    public void setupForRealTests() {
        System.out.println("[TAGGED HOOKS] Running real test (smoke/regression, not wip)");
    }
}

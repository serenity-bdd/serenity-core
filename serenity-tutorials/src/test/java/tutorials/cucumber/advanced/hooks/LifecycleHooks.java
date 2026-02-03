package tutorials.cucumber.advanced.hooks;

import io.cucumber.java.*;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

/**
 * Demonstrates various Cucumber hook patterns.
 * These hooks manage the lifecycle of Screenplay actors and test state.
 */
public class LifecycleHooks {

    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";

    /**
     * Runs once before ALL scenarios in the test run.
     * Use for expensive one-time setup like starting containers.
     */
    @BeforeAll
    public static void beforeAllScenarios() {
        System.out.println("[HOOKS] Starting test suite - BeforeAll");
    }

    /**
     * Runs before EACH scenario.
     * Sets up the Screenplay stage with actors.
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("[HOOKS] Starting scenario: " + scenario.getName());

        // Initialize the Screenplay stage with a cast that can call APIs
        OnStage.setTheStage(new OnlineCast());
    }

    /**
     * Runs after EACH scenario.
     * Cleans up actors and captures failure info.
     */
    @After
    public void afterScenario(Scenario scenario) {
        System.out.println("[HOOKS] Finished scenario: " + scenario.getName()
            + " - Status: " + scenario.getStatus());

        if (scenario.isFailed()) {
            System.out.println("[HOOKS] Scenario failed - would capture additional diagnostics here");
        }

        // Clean up the stage
        OnStage.drawTheCurtain();
    }

    /**
     * Runs once after ALL scenarios in the test run.
     * Use for cleanup of shared resources.
     */
    @AfterAll
    public static void afterAllScenarios() {
        System.out.println("[HOOKS] Test suite complete - AfterAll");
    }
}

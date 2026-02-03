package tutorials.cucumber.advanced.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Demonstrates hook ordering.
 *
 * For @Before hooks: lower order values run FIRST (1, 2, 3...)
 * For @After hooks: higher order values run FIRST (...3, 2, 1)
 *
 * Think of it as wrapping: setup builds up, teardown unwinds in reverse.
 */
public class OrderedHooks {

    // ============== BEFORE HOOKS (lower order runs first) ==============

    @Before(order = 10)
    public void firstSetup() {
        System.out.println("[ORDERED] Before order=10: Initialize environment");
    }

    @Before(order = 20)
    public void secondSetup() {
        System.out.println("[ORDERED] Before order=20: Setup test data");
    }

    @Before(order = 30)
    public void thirdSetup() {
        System.out.println("[ORDERED] Before order=30: Configure application");
    }

    // ============== AFTER HOOKS (higher order runs first) ==============

    @After(order = 30)
    public void firstCleanup() {
        System.out.println("[ORDERED] After order=30: Capture final state");
    }

    @After(order = 20)
    public void secondCleanup() {
        System.out.println("[ORDERED] After order=20: Cleanup test data");
    }

    @After(order = 10)
    public void thirdCleanup() {
        System.out.println("[ORDERED] After order=10: Close resources");
    }
}

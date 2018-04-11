package net.thucydides.core.reports.saucelabs;

import net.thucydides.core.model.TestOutcome;

/**
 * Generate the link to an external system
 */
public interface LinkGenerator {
    public String linkFor(TestOutcome testOutcome);
}

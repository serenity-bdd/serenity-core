package net.thucydides.model.reports.remoteTesting;

import net.thucydides.model.domain.TestOutcome;

/**
 * Generate the link to an external system
 */
public interface LinkGenerator {
    String linkFor(TestOutcome testOutcome);
}

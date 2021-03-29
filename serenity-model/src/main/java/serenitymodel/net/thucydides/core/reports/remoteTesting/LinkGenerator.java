package serenitymodel.net.thucydides.core.reports.remoteTesting;

import serenitymodel.net.thucydides.core.model.TestOutcome;

/**
 * Generate the link to an external system
 */
public interface LinkGenerator {
    public String linkFor(TestOutcome testOutcome);
}

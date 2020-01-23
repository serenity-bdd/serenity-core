package net.thucydides.core.reports.remoteTesting;

import com.google.inject.Inject;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;

public class RemoteTestingLinkManager implements LinkGenerator {
    private EnvironmentVariables environmentVariables;

    //no arg constructor for serialization
    public RemoteTestingLinkManager() {
    }

    @Inject
    public RemoteTestingLinkManager(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String linkFor(TestOutcome testOutcome) {
        if (noSessionIdIsFoundFor(testOutcome)) {
            return null;
        }

        if (browserStackIsConfigured() && testOutcome.getExternalLink() != null) {
            return testOutcome.getExternalLink().getUrl();
        }

        if (sauceLabsIsConfigured()) {
            SaucelabsLinkGenerator saucelabsLinkGenerator = new SaucelabsLinkGenerator(environmentVariables);
            return saucelabsLinkGenerator.linkFor(testOutcome);
        }

        return null;
    }

    private boolean noSessionIdIsFoundFor(TestOutcome testOutcome) {
        return testOutcome.getSessionId() == null;
    }

    private boolean browserStackIsConfigured() {
        return (ThucydidesSystemProperty.BROWSERSTACK_USER.from(environmentVariables) != null);
    }

    private boolean sauceLabsIsConfigured() {
        return (ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables) != null);
    }

}

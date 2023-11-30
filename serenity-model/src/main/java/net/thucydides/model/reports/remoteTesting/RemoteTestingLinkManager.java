package net.thucydides.model.reports.remoteTesting;


import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;

public class RemoteTestingLinkManager implements LinkGenerator {
    private EnvironmentVariables environmentVariables;

    //no arg constructor for serialization
    public RemoteTestingLinkManager() {
    }


    public RemoteTestingLinkManager(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String linkFor(TestOutcome testOutcome) {
        if (noSessionIdIsFoundFor(testOutcome)) {
            return null;
        } else {
            return testOutcome.getExternalLink().getUrl();
        }
//
//        if (browserStackIsConfigured() && testOutcome.getExternalLink() != null) {
//            return testOutcome.getExternalLink().getUrl();
//        }
//
//        return null;
    }

    private boolean noSessionIdIsFoundFor(TestOutcome testOutcome) {
        return testOutcome.getSessionId() == null;
    }

    private boolean browserStackIsConfigured() {
        return (ThucydidesSystemProperty.BROWSERSTACK_USER.from(environmentVariables) != null
                || System.getenv(ThucydidesSystemProperty.BROWSERSTACK_USER.getPropertyName()) != null);
    }
}

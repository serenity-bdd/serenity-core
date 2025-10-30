package net.thucydides.model.reports.remoteTesting;


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
        if (sessionIdIsFoundFor(testOutcome) && testOutcome.getExternalLink() != null) {
            return testOutcome.getExternalLink().getUrl();
        }
        return null;
    }

    private boolean sessionIdIsFoundFor(TestOutcome testOutcome) {
        return testOutcome.getSessionId() != null;
    }
}

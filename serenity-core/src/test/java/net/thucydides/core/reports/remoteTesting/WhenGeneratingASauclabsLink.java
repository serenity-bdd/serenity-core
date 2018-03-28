package net.thucydides.core.reports.remoteTesting;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

/**
 * Browserstack tests for link generation.
 * User: johnsmart, bebef1987
 * Date: 8/03/12
 * Time: 8:18 AM
 */
public class WhenGeneratingASauclabsLink {

    EnvironmentVariables environmentVariables;

    private final String A_SESSION_ID = "5f9fef27854ca50a3c132ce331cb6034";

    @Mock
    TestOutcome testOutcome;

    @Mock
    TestOutcome testOutcomeWithNoSessionId;

    @Before
    public void initMocks() {

        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        when(testOutcome.getSessionId()).thenReturn(A_SESSION_ID);
    }

    @Test
    public void should_generate_a_simple_link_if_no_API_key_is_provided() {
        environmentVariables.setProperty("saucelabs.url","http://username:accesskey@ondemand.remoteTesting.com:80/wd/hub");
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String saucelabsLink = remoteTestingLinkManager.linkFor(testOutcome);
        assertThat(saucelabsLink, is("http://saucelabs.com/jobs/" + A_SESSION_ID));
    }

    @Test
    public void should_generate_no_link_if_the_session_id_is_unavailable() {
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String saucelabsLink = remoteTestingLinkManager.linkFor(testOutcomeWithNoSessionId);
        assertThat(saucelabsLink, is(nullValue()));
    }

    @Test
    public void should_generate_no_link_if_the_saucelabs_links_are_not_activated() {
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String saucelabsLink = remoteTestingLinkManager.linkFor(testOutcome);
        assertThat(saucelabsLink, is(nullValue()));
    }

    @Test
    public void should_generate_a_link_with_hmac_authentication_if_an_API_key_is_provided() {
        environmentVariables.setProperty("saucelabs.url","http://username:accesskey@ondemand.remoteTesting.com:80/wd/hub");
        environmentVariables.setProperty("saucelabs.user.id","example_user");
        environmentVariables.setProperty("saucelabs.access.key","123456-asdf-8dcf81f1fc71");

        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String saucelabsLink = remoteTestingLinkManager.linkFor(testOutcome);

        assertThat(saucelabsLink, is("https://saucelabs.com/jobs/5f9fef27854ca50a3c132ce331cb6034?auth=3fca4184e106622adf2d33d8023271c1"));
    }

}

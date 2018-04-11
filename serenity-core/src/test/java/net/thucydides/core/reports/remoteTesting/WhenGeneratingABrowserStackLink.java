package net.thucydides.core.reports.remoteTesting;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.when;

/**
 * Browserstack tests for link generation.
 * User: johnsmart, bebef1987
 * Date: 8/03/12
 * Time: 8:18 AM
 */
public class WhenGeneratingABrowserStackLink {

    EnvironmentVariables environmentVariables;

    private final String A_SESSION_ID = "18bcc5e42415233ea7a31d11119cfface0ddd443";

    @Mock
    TestOutcome testOutcome;

    @Mock
    TestOutcome testOutcomeWithNoSessionId;

    @Mock
    BrowserStackLinkGenerator browserStackLinkGenerator;

    @Before
    public void initMocks() {

        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        when(testOutcome.getSessionId()).thenReturn(A_SESSION_ID);
    }

    @Test
    public void should_generate_no_link_if_no_user_name_is_provided() {
        environmentVariables.setProperty("browserstack.url","http://:API_key@hub.browserstack.com:80/wd/hub");
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String browserStackLink = remoteTestingLinkManager.linkFor(testOutcome);
        assertThat(browserStackLink, is(nullValue()));
    }

    @Test
    public void should_generate_no_link_if_no_API_key_is_provided() {
        environmentVariables.setProperty("browserstack.url","http://username:@hub.browserstack.com:80/wd/hub");
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String browserStackLink = remoteTestingLinkManager.linkFor(testOutcome);
        assertThat(browserStackLink, is(nullValue()));
    }

    @Test
    public void should_generate_no_link_if_the_session_id_is_unavailable() {
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String browserStackLink = remoteTestingLinkManager.linkFor(testOutcomeWithNoSessionId);
        assertThat(browserStackLink, is(nullValue()));
    }

    @Test
    public void should_generate_no_link_if_the_BrowserStack_is_not_activated() {
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String browserStackLink = remoteTestingLinkManager.linkFor(testOutcome);
        assertThat(browserStackLink, is(nullValue()));
    }

    @Test
    public void should_generate_link_if_browserstack_url_is_provided() {

        Assume.assumeTrue(System.getenv().containsKey("browserstack.username"));
        Assume.assumeTrue(System.getenv().containsKey("browserstack.api_key"));
        Assume.assumeTrue(System.getenv().containsKey("browserstack.sessionID"));

        String username = System.getenv("browserstack.username");
        String apikey = System.getenv("browserstack.api_key");
        String sessionID = System.getenv("browserstack.sessionID");

        when(testOutcome.getSessionId()).thenReturn(sessionID);

        environmentVariables.setProperty("browserstack.url", "http://" + username + ":" + apikey + "@hub.browserstack.com:80/wd/hub");
        RemoteTestingLinkManager remoteTestingLinkManager = new RemoteTestingLinkManager(environmentVariables);

        String browserStackLink = remoteTestingLinkManager.linkFor(testOutcome);

        assertThat(browserStackLink.matches("^https:\\/\\/www.browserstack.com\\/automate\\/builds\\/\\w*\\/sessions\\/\\w*\\?auth_token=\\w*"), is(true));
    }
}

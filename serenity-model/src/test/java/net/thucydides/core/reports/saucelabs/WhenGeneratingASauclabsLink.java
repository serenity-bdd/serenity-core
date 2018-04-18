package net.thucydides.core.reports.saucelabs;

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
 * A description goes here.
 * User: johnsmart
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
        environmentVariables.setProperty("saucelabs.url","http://username:accesskey@ondemand.saucelabs.com:80/wd/hub");
        SaucelabsLinkGenerator saucelabsLinkGenerator = new SaucelabsLinkGenerator(environmentVariables);

        String saucelabsLink = saucelabsLinkGenerator.linkFor(testOutcome);
        assertThat(saucelabsLink, is("http://saucelabs.com/jobs/" + A_SESSION_ID));
    }

    @Test
    public void should_generate_no_link_if_the_session_id_is_unavailable() {
        SaucelabsLinkGenerator saucelabsLinkGenerator = new SaucelabsLinkGenerator(environmentVariables);

        String saucelabsLink = saucelabsLinkGenerator.linkFor(testOutcomeWithNoSessionId);
        assertThat(saucelabsLink, is(nullValue()));
    }

    @Test
    public void should_generate_no_link_if_the_saucelabs_links_are_not_activated() {
        SaucelabsLinkGenerator saucelabsLinkGenerator = new SaucelabsLinkGenerator(environmentVariables);

        String saucelabsLink = saucelabsLinkGenerator.linkFor(testOutcome);
        assertThat(saucelabsLink, is(nullValue()));
    }

    @Test
    public void should_generate_a_link_with_hmac_authentication_if_an_API_key_is_provided() {
        environmentVariables.setProperty("saucelabs.url","http://username:accesskey@ondemand.saucelabs.com:80/wd/hub");
        environmentVariables.setProperty("saucelabs.user.id","example_user");
        environmentVariables.setProperty("saucelabs.access.key","123456-asdf-8dcf81f1fc71");

        SaucelabsLinkGenerator saucelabsLinkGenerator = new SaucelabsLinkGenerator(environmentVariables);

        String saucelabsLink = saucelabsLinkGenerator.linkFor(testOutcome);

        assertThat(saucelabsLink, is("https://saucelabs.com/jobs/5f9fef27854ca50a3c132ce331cb6034?auth=3fca4184e106622adf2d33d8023271c1"));
    }

}

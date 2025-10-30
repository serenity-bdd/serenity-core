package net.thucydides.core.pages.jquery;

import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenAddingJQuerySupportToPageObjects {

    MockEnvironmentVariables environmentVariables;

    @Mock
    WebDriver driver;

    class TestableJQueryEnabledPage extends JQueryEnabledPage {

        public List<String> executedScripts = new ArrayList<String>();

        TestableJQueryEnabledPage(WebDriver driver, EnvironmentVariables environmentVariables) {
            super(driver, environmentVariables);
        }

        @Override
        protected void executeScriptFrom(String scriptSource) {
            executedScripts.add(scriptSource);
        }
    }

    TestableJQueryEnabledPage page;

    @Before
    public void initMocks() {
        environmentVariables = new MockEnvironmentVariables();

        MockitoAnnotations.initMocks(this);

        page = new TestableJQueryEnabledPage(driver, environmentVariables);
    }

    @Test
    public void should_add_the_jquery_library_to_a_page() {

        page.injectJQuery();

        assertThat(page, executedScript("jquery.min.js"));

    }

    private Matcher<? super TestableJQueryEnabledPage> executedScript(String scriptName) {
        return new ExecutedScriptMatcher(scriptName, true);
    }

    private Matcher<? super TestableJQueryEnabledPage> didNotExecutedScript(String scriptName) {
        return new ExecutedScriptMatcher(scriptName, false);
    }

    private static class ExecutedScriptMatcher extends TypeSafeMatcher<TestableJQueryEnabledPage> {
        private final String scriptName;
        private final boolean shouldHaveExecutedScript;

        private ExecutedScriptMatcher(String scriptName, boolean shouldHaveExecutedScript) {
            this.scriptName = scriptName;
            this.shouldHaveExecutedScript = shouldHaveExecutedScript;
        }


        @Override
        protected boolean matchesSafely(TestableJQueryEnabledPage page) {
            if (shouldHaveExecutedScript) {
                return page.executedScripts.stream()
                        .anyMatch(script -> script.contains(scriptName));
            } else {
                return !page.executedScripts.stream()
                        .anyMatch(script -> script.contains(scriptName));
            }
        }

        public void describeTo(Description description) {
            if (shouldHaveExecutedScript) {
                description.appendText("Should have executed script").appendValue(scriptName);
            } else {
                description.appendText("Should not have executed script").appendValue(scriptName);
            }
        }
    }

    @Test
    public void should_not_add_the_highlighting_plugin_by_default() {
        page.injectJQueryPlugins();

        assertThat(page, didNotExecutedScript("jquery-thucydides-plugin.js"));
    }


    @Test
    public void should_add_the_highlighting_plugin_if_configured() {
        environmentVariables.setProperty("thucydides.activate.highlighting", "true");

        page.injectJQueryPlugins();

        assertThat(page, executedScript("jquery-thucydides-plugin.js"));
    }


    @Test
    public void should_not_add_the_jquery_library_to_a_page_if_jquery_integration_is_deactivated() {
        environmentVariables.setProperty("thucydides.jquery.integration", "false");

        assertThat(page.isJQueryIntegrationEnabled(), is(false));
    }

}

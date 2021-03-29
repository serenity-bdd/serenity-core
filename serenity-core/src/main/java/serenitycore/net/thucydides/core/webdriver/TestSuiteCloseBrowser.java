package serenitycore.net.thucydides.core.webdriver;

import serenitymodel.net.serenitybdd.core.collect.NewList;
import serenitycore.net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach;
import serenitycore.net.thucydides.core.annotations.TestCaseAnnotations;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

public class TestSuiteCloseBrowser extends WebdriverCloseBrowser implements CloseBrowser {
    private final Class<?> testSuite;

    private final static List<RestartBrowserForEach> SCENARIO_EVENTS = NewList.of(
            RestartBrowserForEach.SCENARIO, RestartBrowserForEach.EXAMPLE
    );

    public TestSuiteCloseBrowser(EnvironmentVariables environmentVariables, Class<?> testSuite) {
        super(environmentVariables);
        this.testSuite = testSuite;
    }

    @Override
    public void closeIfConfiguredForANew(RestartBrowserForEach event) {

        if (SCENARIO_EVENTS.contains(event) && useUniqueBrowserSessionForScenarios()) {
            return;
        }

        super.closeIfConfiguredForANew(event);
    }

    private boolean useUniqueBrowserSessionForScenarios() {
        return TestCaseAnnotations.isUniqueSession(testSuite);
    }

}

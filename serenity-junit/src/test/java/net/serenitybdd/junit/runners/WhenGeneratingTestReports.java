package net.serenitybdd.junit.runners;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.serenitybdd.junit.rules.QuietThucydidesLoggingRule;
import net.serenitybdd.junit.samples.AnnotatedSingleTestScenario;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Managing the WebDriver instance during a test run The instance should be
 * created once at the start of the test run, and closed once at the end of the
 * tets.
 *
 * @author johnsmart
 *
 */
public class WhenGeneratingTestReports extends AbstractTestStepRunnerTest {

    @Mock
    AcceptanceTestReporter mockReporter;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    @Test
    public void a_test_reporter_can_subscribe_to_the_runner() throws Exception {

        SerenityRunner runner = new SerenityRunner(AnnotatedSingleTestScenario.class);
        runner.subscribeReporter(mockReporter);

        runner.run(new RunNotifier());

        verify(mockReporter).generateReportFor(any(TestOutcome.class));
    }
}

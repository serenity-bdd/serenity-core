package net.thucydides.junit.runners;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.samples.AnnotatedSingleTestScenario;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    public void a_test_reporter_can_subscribe_to_the_runner() throws InitializationError, IOException {
        
        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.subscribeReporter(mockReporter);

        runner.run(new RunNotifier());

        verify(mockReporter).generateReportFor(any(TestOutcome.class), any(TestOutcomes.class));
    }


    @Test
    public void the_runner_should_tell_the_reporter_what_directory_to_use()
            throws InitializationError, IOException {
        
        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        runner.subscribeReporter(mockReporter);

        runner.run(new RunNotifier());

        verify(mockReporter,atLeast(1)).setOutputDirectory(any(File.class));
    }
    
    @Test
    public void multiple_test_reporters_can_subscribe_to_the_runner()
            throws InitializationError, IOException {

        ThucydidesRunner runner = new ThucydidesRunner(AnnotatedSingleTestScenario.class);
        
        AcceptanceTestReporter reporter1 = mock(AcceptanceTestReporter.class);
        AcceptanceTestReporter reporter2 = mock(AcceptanceTestReporter.class);

        runner.subscribeReporter(reporter1);
        runner.subscribeReporter(reporter2);

        runner.run(new RunNotifier());

        verify(reporter1).generateReportFor(any(TestOutcome.class), any(TestOutcomes.class));
        verify(reporter2).generateReportFor(any(TestOutcome.class), any(TestOutcomes.class));
    } 

}

package net.serenitybdd.maven.plugins;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.reports.ResultChecker;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenCheckingTheMavenTestResults {

    SerenityCheckMojo plugin;

    @Mock
    ResultChecker resultChecker;

    @Before
    public void setupPlugin() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = MojoFailureException.class)
    public void should_report_errors_if_present() throws Exception {

        when(resultChecker.checkTestResults()).thenReturn(TestResult.ERROR);

        plugin = new SerenityCheckMojo() {
            @Override
            protected ResultChecker getResultChecker() {
                return resultChecker;
            }
        };
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_report_failures_if_present() throws Exception {

        when(resultChecker.checkTestResults()).thenReturn(TestResult.FAILURE);

        plugin = new SerenityCheckMojo() {
            @Override
            protected ResultChecker getResultChecker() {
                return resultChecker;
            }
        };
        plugin.execute();
    }

    @Test(expected = MojoFailureException.class)
    public void should_report_compromised_tests_if_present() throws Exception {

        when(resultChecker.checkTestResults()).thenReturn(TestResult.COMPROMISED);

        plugin = new SerenityCheckMojo() {
            @Override
            protected ResultChecker getResultChecker() {
                return resultChecker;
            }
        };
        plugin.execute();
    }

    public void should_report_successful_results_without_an_exception() throws Exception {

        when(resultChecker.checkTestResults()).thenReturn(TestResult.SUCCESS);

        plugin = new SerenityCheckMojo() {
            @Override
            protected ResultChecker getResultChecker() {
                return resultChecker;
            }
        };
        plugin.execute();
    }
}

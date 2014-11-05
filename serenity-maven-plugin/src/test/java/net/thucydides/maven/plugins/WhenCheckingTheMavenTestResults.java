package net.thucydides.maven.plugins;

import net.thucydides.core.reports.ResultChecker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class WhenCheckingTheMavenTestResults {

    ThucydidesCheckMojo plugin;

    @Mock
    ResultChecker resultChecker;

    @Before
    public void setupPlugin() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_report_failures_if_present() throws Exception {

        plugin = new ThucydidesCheckMojo() {
            @Override
            protected ResultChecker getResultChecker() {
                return resultChecker;
            }
        };
        plugin.execute();

        verify(resultChecker).checkTestResults();
    }


}

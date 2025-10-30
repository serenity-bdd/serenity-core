package net.serenitybdd.junit.runners;

import net.thucydides.core.webdriver.DriverConfiguration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.model.batches.BatchManager;
import net.thucydides.model.domain.TestOutcome;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

/**
 * User: YamStranger
 * Date: 3/9/16
 * Time: 4:28 PM
 */
abstract class QualifiedTestsRunner extends SerenityRunner {
    private String qualifier;
    private Object test;

    public QualifiedTestsRunner(final Class<?> type,
                         final DriverConfiguration configuration,
                         final WebDriverFactory webDriverFactory,
                         final BatchManager batchManager) throws InitializationError {
        super(type, webDriverFactory, configuration, batchManager);
    }

    @Override
    public final Object createTest() throws Exception {
        this.test = initializeTest();
        return this.test;
    }

    abstract protected Object initializeTest() throws Exception;

    @Override
    public final void useQualifier(final String qualifier) {
        this.qualifier = qualifier;
        super.useQualifier(qualifier);
    }

    @Override
    public final List<TestOutcome> getTestOutcomes() {
        return enhance(qualified(super.getTestOutcomes()));
    }

    public List<TestOutcome> enhance(List<TestOutcome> outcomes) {
        return outcomes;
    }

    private List<TestOutcome> qualified(List<TestOutcome> testOutcomes) {
        List<TestOutcome> qualifiedOutcomes = new ArrayList<>();

        if (this.test != null) {
            useQualifier(QualifierFinder.forTestCase(test).getQualifier());
        }

        for (TestOutcome outcome : testOutcomes) {
            qualifiedOutcomes.add(outcome.withQualifier(qualifier));
        }
        return qualifiedOutcomes;
    }
}

package net.thucydides.junit.runners;

import com.google.common.collect.Lists;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.model.DataTableRow;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

class TestClassRunnerForParameters extends ThucydidesRunner {
    private final int parameterSetNumber;
    private final DataTable parametersTable;
    private String qualifier;

    TestClassRunnerForParameters(final Class<?> type,
                                 final Configuration configuration,
                                 final WebDriverFactory webDriverFactory,
                                 final BatchManager batchManager,
                                 final DataTable parametersTable,
                                 final int i) throws InitializationError {
        super(type, webDriverFactory, configuration, batchManager);
        this.parametersTable = parametersTable;
        parameterSetNumber = i;
    }

    @Override
    protected JUnitStepListener initListenersUsing(final Pages pageFactory) {
        setStepListener(JUnitStepListener.withOutputDirectory(getConfiguration().getOutputDirectory())
                .and().withPageFactory(pageFactory)
                .and().withParameterSetNumber(parameterSetNumber)
                .and().withParametersTable(parametersTable)
                .and().withTestClass(getTestClass().getJavaClass())
                .and().build());
        return getStepListener();
    }

    @Override
    public Object createTest() throws Exception {
        return getTestClass().getOnlyConstructor().newInstance(computeParams());
    }

    private Object[] computeParams() throws Exception {
        try {
            DataTableRow row = parametersTable.getRows().get(parameterSetNumber);
            return row.getValues().toArray();
        } catch (ClassCastException cause) {
            throw new Exception(String.format(
                    "%s.%s() must return a Collection of arrays.",
                    getTestClass().getName(),
                    DataDrivenAnnotations.forClass(getTestClass()).getTestDataMethod().getName()),
                    cause);
        }
    }

    @Override
    protected String getName() {
        String firstParameter = parametersTable.getRows().get(parameterSetNumber).getValues().get(0).toString();
        return String.format("[%s]", firstParameter);
    }

    @Override
    protected String testName(final FrameworkMethod method) {
        return String.format("%s[%s]", method.getName(), parameterSetNumber);
    }

    @Override
    protected void validateConstructor(final List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
    }

    @Override
    protected Statement classBlock(final RunNotifier notifier) {
        return childrenInvoker(notifier);
    }

    @Override
    protected void generateReports() {
        //do not generate reports at example level
    }

    @Override
    public void useQualifier(final String qualifier) {
        this.qualifier = qualifier;
        super.useQualifier(qualifier);
    }

    @Override
    public List<TestOutcome> getTestOutcomes() {
        return qualified(super.getTestOutcomes());
    }

    private List<TestOutcome> qualified(List<TestOutcome> testOutcomes) {
        List<TestOutcome> qualifiedOutcomes = Lists.newArrayList();
        for(TestOutcome outcome : testOutcomes) {
            qualifiedOutcomes.add(outcome.withQualifier(qualifier));
        }
        return qualifiedOutcomes;
    }


}

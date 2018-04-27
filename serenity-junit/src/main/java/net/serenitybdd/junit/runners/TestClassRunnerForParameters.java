package net.serenitybdd.junit.runners;

import net.thucydides.core.batches.*;
import net.thucydides.core.model.*;
import net.thucydides.core.pages.*;
import net.thucydides.core.webdriver.*;
import net.thucydides.junit.listeners.*;
import org.junit.runner.notification.*;
import org.junit.runners.model.*;

import java.util.*;

class TestClassRunnerForParameters extends QualifiedTestsRunner {
    private final int parameterSetNumber;
    private final DataTable parametersTable;

    TestClassRunnerForParameters(final Class<?> type,
                                 final DriverConfiguration configuration,
                                 final WebDriverFactory webDriverFactory,
                                 final BatchManager batchManager,
                                 final DataTable parametersTable,
                                 final int i) throws InitializationError {
        super(type, configuration, webDriverFactory, batchManager);
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
    protected JUnitStepListener initListeners() {
        setStepListener(JUnitStepListener.withOutputDirectory(getConfiguration().getOutputDirectory())
                .and().withParameterSetNumber(parameterSetNumber)
                .and().withParametersTable(parametersTable)
                .and().withTestClass(getTestClass().getJavaClass())
                .and().build());
        return getStepListener();
    }

    @Override
    protected Object initializeTest() throws Exception {
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
}

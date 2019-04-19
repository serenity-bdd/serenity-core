package net.serenitybdd.junit.runners;

import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepListener;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class ParameterizedJUnitStepListener extends JUnitStepListener {

    final int parameterSetNumber;
    private final DataTable parametersTable;

    public ParameterizedJUnitStepListener(final int parameterSetNumber, final DataTable parametersTable,
                                          final Class<?> testClass, BaseStepListener baseStepListener,
                                          StepListener... listeners) {
        super(testClass, baseStepListener, listeners);
        this.parameterSetNumber = parameterSetNumber;
        this.parametersTable = parametersTable;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {

        super.testRunStarted(description);
    }


    @Override
    public void testStarted(final Description description) {
        if (testingThisDataSet(description)) {
            super.testStarted(description);
            StepEventBus.getEventBus().useExamplesFrom(dataTableRow());
            if (!ignoredOrPending(description))
                StepEventBus.getEventBus().exampleStarted(parametersTable.row(parameterSetNumber).toStringMap());
        }
    }

    private boolean isPending(Description description) {
        return  TestAnnotations.forClass(description.getTestClass()).isPending(description.getMethodName());
    }

    private boolean isIgnored(Description description) {
        return  TestAnnotations.forClass(description.getTestClass()).isIgnored(description.getMethodName());
    }

    private boolean ignoredOrPending(Description description) {
        return isIgnored(description) || isPending(description);
    }

    private DataTable dataTableRow() {
        return DataTable.withHeaders(parametersTable.getHeaders()).andCopyRowDataFrom(parametersTable.getRows().get(parameterSetNumber)).build();
    }

    private DataTable dataTable() {
        return DataTable.withHeaders(parametersTable.getHeaders()).andRowData (parametersTable.getRows()).build();
    }

    private boolean testingThisDataSet(Description description) {
        if (description == null || description.getTestClass() == null) { return false; }

        return ((description.getTestClass().equals(getTestClass())) &&
                (description.getMethodName().endsWith("[" + parameterSetNumber + "]")));
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        if (testingThisDataSet(description)) {
            super.testFinished(description);
            StepEventBus.getEventBus().exampleFinished();
        }
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        if (testingThisDataSet(failure.getDescription())) {
            super.testFailure(failure);
            StepEventBus.getEventBus().exampleFinished();
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        if (testingThisDataSet(description))
        {
            super.testIgnored(description);
            if (!ignoredOrPending(description)){
                StepEventBus.getEventBus().exampleFinished();
            }
        }
    }

}

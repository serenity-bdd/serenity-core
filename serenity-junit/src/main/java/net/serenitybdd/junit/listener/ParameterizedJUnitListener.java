package net.serenitybdd.junit.listener;

import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepListener;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

public class ParameterizedJUnitListener extends JUnitListener{
    final int parameterSetNumber;
    private final DataTable parametersTable;

    public ParameterizedJUnitListener(final int parameterSetNumber, final DataTable parametersTable,
                                      final TestIdentifier testClass, BaseStepListener baseStepListener,
                                      StepListener... listeners) {
        super(testClass, baseStepListener, listeners);
        this.parameterSetNumber = parameterSetNumber;
        this.parametersTable = parametersTable;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan){
        super.testPlanExecutionStarted(testPlan);
    }
    @Override
    public void executionStarted(TestIdentifier testIdentifier){
        if (testingThisDataSet(testIdentifier)) {
            super.executionStarted(testIdentifier);
            StepEventBus.getEventBus().useExamplesFrom(dataTableRow());
            if (disabledOrPending(testIdentifier))
                StepEventBus.getEventBus().exampleStarted(parametersTable.row(parameterSetNumber).toStringMap());
        }
    }
    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult){
        if (testingThisDataSet(testIdentifier)) {
            super.executionFinished(testIdentifier, testExecutionResult);
            StepEventBus.getEventBus().exampleFinished();
        }
    }
    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason){
        if (testingThisDataSet(testIdentifier)) {
            super.executionSkipped(testIdentifier, reason);
            if (disabledOrPending(testIdentifier)) {
                StepEventBus.getEventBus().exampleFinished();
            }
        }
    }

    private boolean isPending(TestIdentifier testIdentifier) {
        return  TestAnnotations.forClass(testIdentifier.getClass()).isPending(testIdentifier.getDisplayName());
    }

    private boolean isDisabled(TestIdentifier testIdentifier) {
        return  TestAnnotations.forClass(testIdentifier.getClass()).isDisabled(testIdentifier.getDisplayName());
    }

    private boolean disabledOrPending(TestIdentifier testIdentifier) {
        return !isDisabled(testIdentifier) && !isPending(testIdentifier);
    }

    private DataTable dataTableRow() {
        return DataTable.withHeaders(parametersTable.getHeaders()).andCopyRowDataFrom(parametersTable.getRows().get(parameterSetNumber)).build();
    }

    private DataTable dataTable() {
        return DataTable.withHeaders(parametersTable.getHeaders()).andRowData (parametersTable.getRows()).build();
    }

    private boolean testingThisDataSet(TestIdentifier testIdentifier) {
        if (testIdentifier == null) { return false; }

        return ((testIdentifier.equals(getTestClass())) &&
                (testIdentifier.getDisplayName().endsWith("[" + parameterSetNumber + "]")));
    }
}

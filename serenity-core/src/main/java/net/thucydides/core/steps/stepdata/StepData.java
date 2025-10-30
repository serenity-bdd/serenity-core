package net.thucydides.core.steps.stepdata;

import net.thucydides.core.steps.DataDrivenStepFactory;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.model.domain.DataTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data-driven test step execution.
 */
public final class StepData {

    public static final char DEFAULT_SEPARATOR = ',';
    private StepFactory factory;
    TestDataSource testdata;

    private static final ThreadLocal<StepFactory> factoryThreadLocal = new ThreadLocal<StepFactory>();

    public StepData(final TestDataSource testDataSource) throws IOException {
        testdata = testDataSource;
    }

    public static StepData withTestDataFrom(final String testDataSource) throws IOException {
        return new StepData(new CSVTestDataSource(testDataSource, DEFAULT_SEPARATOR));
    }

    public static StepData withTestDataFrom(final String... testDataRows) throws IOException {
        return new StepData(new StringTestDataSource(testDataRows));
    }

    @SuppressWarnings("unchecked")
    public <T> T run(final T steps) throws IOException {

        useDefaultStepFactoryIfUnassigned();

        StepEventBus.getParallelEventBus().useExamplesFrom(dataTable(testdata));

        Class<?> scenarioStepsClass = steps.getClass().getSuperclass();
        List<T> instanciatedSteps = (List<T>) testdata.getInstanciatedInstancesFrom(scenarioStepsClass, factory);

        DataDrivenStepFactory dataDrivenStepFactory = new DataDrivenStepFactory(factory);
        T stepsProxy = (T) dataDrivenStepFactory.newDataDrivenSteps(scenarioStepsClass, instanciatedSteps);

        return stepsProxy;
    }

    private DataTable dataTable(TestDataSource testdata) {
        return DataTable.withHeaders(testdata.getHeaders())
                        .andRows(rowsFrom(testdata)).build();
    }

    private List<List<Object>> rowsFrom(TestDataSource testdata) {
        List<List<Object>> rows = new ArrayList<>();
        for (Map<String,String> rowData : testdata.getData()) {
            List<Object> row = new ArrayList<>();
            for(String header : testdata.getHeaders()) {
                row.add(rowData.get(header));
            }
            rows.add(row);
        }
        return rows;
    }

    private void useDefaultStepFactoryIfUnassigned() {
        if (factory == null) {
            factory = getDefaultStepFactory();
        }
    }

    public StepData usingFactory(final StepFactory factory) {
        this.factory = factory;
        return this;
    }

    public static void setDefaultStepFactory(final StepFactory factory) {
        factoryThreadLocal.set(factory);
    }

    public static StepFactory getDefaultStepFactory() {
        if (factoryThreadLocal.get() == null) {
            factoryThreadLocal.set(StepFactory.getFactory());
        }
        return factoryThreadLocal.get();
    }

    public StepData separatedBy(char newSeparator) {
        testdata = testdata.separatedBy(newSeparator);
        return this;
    }
}

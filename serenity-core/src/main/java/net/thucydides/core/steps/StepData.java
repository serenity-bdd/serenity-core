package net.thucydides.core.steps;

import com.google.common.collect.Lists;
import net.thucydides.core.csv.CSVTestDataSource;
import net.thucydides.core.csv.TestDataSource;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Data-driven test step execution.
 */
public final class StepData {

    private final String testDataSource;
    private char separator = ',';
    private StepFactory factory;

    private static final ThreadLocal<StepFactory> factoryThreadLocal = new ThreadLocal<StepFactory>();

    public StepData(final String testDataSource) {
        FilePathParser testDataSourcePath
                = new FilePathParser(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
        this.testDataSource = testDataSourcePath.getInstanciatedPath(testDataSource);
    }

    public static StepData withTestDataFrom(final String testDataSource) {
        return new StepData(testDataSource);
    }

    @SuppressWarnings("unchecked")
    public <T> T run(final T steps) throws IOException {

        useDefaultStepFactoryIfUnassigned();
        TestDataSource testdata = new CSVTestDataSource(testDataSource, separator);

        StepEventBus.getEventBus().useExamplesFrom(dataTable(testdata));

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
        List<List<Object>> rows = Lists.newArrayList();
        for (Map<String,String> rowData : testdata.getData()) {
            List<Object> row = Lists.newArrayList();
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
            factoryThreadLocal.set(new StepFactory());
        }
        return factoryThreadLocal.get();
    }

    public StepData separatedBy(char newSeparator) {
        this.separator = newSeparator;
        return this;
    }
}

package net.thucydides.core.csv;

import net.thucydides.core.steps.StepFactory;

import java.util.List;
import java.util.Map;

/**
 * A set of test data used in parameterized web tests.
 * Test data can come from a number of sources, such as CSV files, Excel spreadsheet, arrays, etc.
 */
public interface TestDataSource {

    List<String> getHeaders();

    List<Map<String, String>> getData();

    <T> List<T> getDataAsInstancesOf(Class<T> clazz, Object... constructorArgs);

    <T> List<T> getInstanciatedInstancesFrom(Class<T> clazz, StepFactory factory);

}

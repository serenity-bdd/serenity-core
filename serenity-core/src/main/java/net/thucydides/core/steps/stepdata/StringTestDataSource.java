package net.thucydides.core.steps.stepdata;

import com.google.common.base.Splitter;
import net.thucydides.core.csv.FailedToInitializeTestData;
import net.thucydides.core.csv.FieldName;
import net.thucydides.core.csv.InstanceBuilder;
import net.thucydides.core.steps.StepFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Test data from a set of Strings.
 */
public class StringTestDataSource implements TestDataSource {

    private char separator;
    private final String header;
    private final List<String> rows;

    private static final Logger LOGGER = LoggerFactory.getLogger(StringTestDataSource.class);

    public StringTestDataSource(final String... rows) {
        this.separator = StepData.DEFAULT_SEPARATOR;
        List<String> rowData = Arrays.asList(rows);
        this.header =  rowData.get(0);
        this.rows = rowData.subList(1, rowData.size());
    }

    private Map<String, String> dataEntryFrom(final List<String> titleRow, final List<String> dataRow) {
        Map<String, String> dataset = new HashMap<>();

        for (int column = 0; column < titleRow.size(); column++) {
            if (column < dataRow.size()) {
                String title = titleRow.get(column).trim();
                String value = dataRow.get(column).trim();
                dataset.put(title, value);
            }
        }

        return dataset;
    }

    public List<Map<String, String>> getData() {
        return loadTestDataFrom(getRows());
    }

    private List<List<String>> getRows() {
        List<List<String>> expandedRows = new ArrayList<>();
        for (String row : rows) {
           expandedRows.add(Splitter.on(separator).splitToList(row));
        }
        return expandedRows;
    }

    public List<String> getHeaders() {
        return Splitter.on(separator).trimResults().splitToList(header);
    }

    protected List<Map<String, String>> loadTestDataFrom(List<List<String>> rows) {

        List<Map<String, String>> loadedData = new ArrayList<>();
        List<String> titleRow = getHeaders();

        for (List<String> dataRow : rows) {
            loadedData.add(dataEntryFrom(titleRow, dataRow));
        }
        return loadedData;
    }


    /**
     * Returns the test data as a list of JavaBean instances.
     */
    public <T> List<T> getDataAsInstancesOf(final Class<T> clazz, final Object... constructorArgs) {
        List<Map<String, String>> data = getData();

        List<T> resultsList = new ArrayList<>();
        for (Map<String, String> rowData : data) {
            resultsList.add(newInstanceFrom(clazz, rowData, constructorArgs));
        }
        return resultsList;
    }

    public <T> List<T> getInstanciatedInstancesFrom(final Class<T> clazz, final StepFactory factory) {
        List<Map<String, String>> data = getData();
        
        List<T> resultsList = new ArrayList<>();
        for (Map<String, String> rowData : data) {
            resultsList.add(newInstanceFrom(clazz, factory, rowData));
        }
        return resultsList;
    }

    @Override
    public TestDataSource separatedBy(char newSeparator) {
        this.separator = newSeparator;
        return this;
    }

    private <T> T newInstanceFrom(final Class<T> clazz,
                                  final Map<String,String> rowData,
                                  final Object... constructorArgs) {

        T newObject = createNewInstanceOf(clazz, constructorArgs);
        assignPropertiesFromTestData(clazz, rowData, newObject);
        return newObject;
    }

    private <T> T newInstanceFrom(final Class<T> clazz,
                                  final StepFactory factory,
                                  final Map<String,String> rowData) {
    	
    	T newObject = factory.getUniqueStepLibraryFor(clazz);
        assignPropertiesFromTestData(clazz, rowData, newObject);
        return newObject;
    }

    private <T> void assignPropertiesFromTestData(final Class<T> clazz,
                                                  final Map<String, String> rowData,
                                                  final T newObject) {
        Set<String> propertyNames = rowData.keySet();

        boolean validPropertyFound = false;
        for (String columnHeading : propertyNames) {
            String value = rowData.get(columnHeading);
            String property = FieldName.from(columnHeading).inNormalizedForm();

            if (assignPropertyValue(newObject, property, value)) {
                validPropertyFound = true;
            }
        }
        if (!validPropertyFound) {
            throw new FailedToInitializeTestData("No properties or public fields matching the data columns were found "
                                                 + "or could be assigned for the class " + clazz.getName()
                                                 + "using test data: " + rowData);
        }
    }

    protected <T> T createNewInstanceOf(final Class<T> clazz, final Object... constructorArgs) {
        try {
            return InstanceBuilder.newInstanceOf(clazz, constructorArgs);
        } catch (Exception e) {
            LOGGER.error("Could not create test data bean", e);
            throw new FailedToInitializeTestData("Could not create test data beans", e);
        }
    }

    protected <T> boolean assignPropertyValue(final T newObject, final String property, final String value) {
        boolean valueWasAssigned = true;
        try {
            InstanceBuilder.inObject(newObject).setPropertyValue(property, value);
        } catch (FailedToInitializeTestData e) {
            valueWasAssigned = false;
        }
        return valueWasAssigned;
    }

}

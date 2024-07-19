package net.thucydides.core.steps.stepdata;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Preconditions;
import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.configuration.FilePathParser;
import net.thucydides.core.csv.FailedToInitializeTestData;
import net.thucydides.core.csv.FieldName;
import net.thucydides.core.csv.InstanceBuilder;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.core.steps.StepFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Test data from a CSV file.
 */
public class CSVTestDataSource implements TestDataSource {

    //private final List<Map<String, String>> testData;
    private char separator;
    private final char quotechar;
    private final char escape;
    private final int skipLines;
    private final List<String> instantiatedPaths;
    private List<String[]> csvDataRows;

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVTestDataSource.class);
    FilePathParser testDataSourcePath = new FilePathParser(SystemEnvironmentVariables.currentEnvironmentVariables() );

    public CSVTestDataSource(final List<String> paths, final char separatorValue, final char quotechar, final char escape, final int skipLines) throws IOException {
        this.separator = separatorValue;
        this.quotechar = quotechar;
        this.escape = escape;
        this.skipLines = skipLines;
        this.instantiatedPaths = instantiated(paths);
    }

    private List<String> instantiated(List<String> paths) {
        List<String> instantiated = new ArrayList<>();
        for(String path : paths) {
            instantiated.add(testDataSourcePath.getInstanciatedPath(path));
        }
        return instantiated;
    }

    List<String[]> getDataRows() {
        if (csvDataRows == null) {
            csvDataRows = new ArrayList<>();
            for(String instantiatedPath : instantiatedPaths) {
                try (Reader reader = getDataFileFor(instantiatedPath)) {
                    csvDataRows.addAll(getCSVDataFrom(reader));
                } catch (IOException e) {
                    LOGGER.error("Could not read test data file from {}", instantiatedPath, e);
                }
            }
        }
        return csvDataRows;
    }

    public CSVTestDataSource(final String path) throws IOException {
        this(NewList.of(path), CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, CSVReader.DEFAULT_SKIP_LINES);
    }

    public CSVTestDataSource(final List<String> paths, final char separatorValue) throws IOException {
        this(paths, separatorValue, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, CSVReader.DEFAULT_SKIP_LINES);
    }

    public CSVTestDataSource(final String path, final char separatorValue) throws IOException {
        this(NewList.of(path), separatorValue, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, CSVReader.DEFAULT_SKIP_LINES);
    }

    public CSVTestDataSource(final String path, final char separatorValue, final char quotechar, final char escape) throws IOException {
        this(NewList.of(path), separatorValue, quotechar, escape, CSVReader.DEFAULT_SKIP_LINES);
    }

    public static boolean validTestDataPath(final String path) {
        if (validFileSystemPath(path) || isAClasspathResource(path)) {
            return true;
        } else {
            URL testDataUrl = CSVTestDataSource.class.getClassLoader().getResource(path);
            return (testDataUrl != null ) && new File(testDataUrl.getFile()).exists();
        }
    }

    private Reader getDataFileFor(final String path) throws FileNotFoundException {
        Preconditions.checkNotNull(path, "Test data source was not defined");
        if (isAClasspathResource(path)) {
            return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
        } else if (validFileSystemPath(path)){
        	return new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
        }
    	throw new FileNotFoundException("Could not load test data from " + path);
    }

    private static boolean isAClasspathResource(final String path) {
    	return !(CSVTestDataSource.class.getClassLoader().getResourceAsStream(path) == null);

    }

    private static boolean validFileSystemPath(final String path) {
        File file = new File(path);
        return file.exists();
    }

    protected List<String[]> getCSVDataFrom(final Reader testDataReader) throws IOException {

        List<String[]> rows;
        try (CSVReader reader = new CSVReader(testDataReader, separator, quotechar, escape, skipLines)) {
            rows = reader.readAll();
        }
        return rows;
    }

    protected List<Map<String, String>> loadTestDataFrom(List<String[]> rows) throws IOException {

        List<Map<String, String>> loadedData = new ArrayList<>();
        String[] titleRow = rows.get(0);

        for (int i = 1; i < rows.size(); i++) {
            String[] dataRow = rows.get(i);
            loadedData.add(dataEntryFrom(titleRow, dataRow));
        }
        return loadedData;
    }


    private Map<String, String> dataEntryFrom(final String[] titleRow, final String[] dataRow) {
        Map<String, String> dataset = new HashMap();

        for (int column = 0; column < titleRow.length; column++) {
            if (column < dataRow.length) {
                String title = titleRow[column].trim();
                String value = dataRow[column].trim();
                dataset.put(title, value);
            }
        }

        return dataset;
    }

    public List<Map<String, String>> getData() {
        List<Map<String, String>> data = new ArrayList<>();
        for(String instantiatedPath : instantiatedPaths) {
            try (Reader reader = getDataFileFor(instantiatedPath)) {
                data.addAll(loadTestDataFrom(getCSVDataFrom(reader)));
            } catch (IOException e) {
                LOGGER.error("Could not read test data file from {}", instantiatedPath, e);
            }
        }
        return data;
    }

    public List<String> getHeaders() {

        return Arrays.stream(getTitleRow())
                .map(StringUtils::strip)
                .collect(Collectors.toList());
    }

    private String[] getTitleRow() {
        return getDataRows().get(0);
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

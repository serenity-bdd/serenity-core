package net.thucydides.core.steps.stepdata;

import au.com.bytecode.opencsv.CSVReader;
import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.csv.FailedToInitializeTestData;
import net.thucydides.core.csv.FieldName;
import net.thucydides.core.csv.InstanceBuilder;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;

import static ch.lambdaj.Lambda.convert;

/**
 * Test data from a CSV file.
 */
public class CSVTestDataSource implements TestDataSource {
    
    //private final List<Map<String, String>> testData;
    private char separator;
    private final char quotechar;
    private final char escape;
    private final int skipLines;
    private final String instantiatedPath;
    private List<String[]> csvDataRows;

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVTestDataSource.class);
    FilePathParser testDataSourcePath = new FilePathParser(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );

    public CSVTestDataSource(final String path, final char separatorValue, final char quotechar, final char escape, final int skipLines) throws IOException {
        this.separator = separatorValue;
        this.quotechar = quotechar;
        this.escape = escape;
        this.skipLines = skipLines;
        this.instantiatedPath = testDataSourcePath.getInstanciatedPath(path);

//        List<String[]> csvDataRows = getCSVDataFrom(getDataFileFor(instantiatedPath));
//        String[] titleRow = csvDataRows.get(0);

//        this.headers = convert(titleRow, new Converter<String, String>() {
//            @Override
//            public String convert(String str) {
//                return StringUtils.strip(str);
//            }
//        });
    }

    List<String[]> getDataRows() {
        if (csvDataRows == null) {
            try {
                csvDataRows = getCSVDataFrom(getDataFileFor(instantiatedPath));
            } catch (IOException e) {
                LOGGER.error("Could not read test data file from {}", instantiatedPath, e);
            }
        }
        return csvDataRows;
    }

    public CSVTestDataSource(final String path) throws IOException {
        this(path, CSVReader.DEFAULT_SEPARATOR, CSVReader.DEFAULT_QUOTE_CHARACTER, CSVReader.DEFAULT_ESCAPE_CHARACTER, CSVReader.DEFAULT_SKIP_LINES);
    }

    public CSVTestDataSource(final String path, final char separatorValue) throws IOException {
        this(path, separatorValue, CSVReader.DEFAULT_QUOTE_CHARACTER, CSVReader.DEFAULT_ESCAPE_CHARACTER, CSVReader.DEFAULT_SKIP_LINES);
    }

    public CSVTestDataSource(final String path, final char separatorValue, final char quotechar, final char escape) throws IOException {
        this(path, separatorValue, quotechar, escape, CSVReader.DEFAULT_SKIP_LINES);
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
        		return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path));
        } else if (validFileSystemPath(path)){
        	return new FileReader(new File(path));
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
        Map<String, String> dataset = new HashMap<>();

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
        try {
            return loadTestDataFrom(getCSVDataFrom(getDataFileFor(instantiatedPath)));
        } catch (IOException e) {
            LOGGER.error("Could not read test data file from {}", instantiatedPath, e);
        }
        return ImmutableList.of();
    }

    public List<String> getHeaders() {
        return convert(getTitleRow(), new Converter<String, String>() {
            @Override
            public String convert(String str) {
                return StringUtils.strip(str);
            }
        });
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

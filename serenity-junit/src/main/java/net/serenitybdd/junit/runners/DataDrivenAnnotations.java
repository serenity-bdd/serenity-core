package net.serenitybdd.junit.runners;

import ch.lambdaj.function.convert.Converter;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.steps.FilePathParser;
import net.thucydides.core.steps.stepdata.CSVTestDataSource;
import net.thucydides.core.steps.stepdata.TestDataSource;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.apache.commons.lang3.StringUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.convert;

public class DataDrivenAnnotations {

    private final EnvironmentVariables environmentVariables;

    private final Pattern DATASOURCE_PATH_SEPARATORS = Pattern.compile("[;,]");

    public static DataDrivenAnnotations forClass(final Class testClass) {
        return new DataDrivenAnnotations(testClass);
    }

    public static DataDrivenAnnotations forClass(final TestClass testClass) {
        return new DataDrivenAnnotations(testClass);
    }

    private final TestClass testClass;

    DataDrivenAnnotations(final Class testClass) {
        this(new TestClass(testClass));
    }

    DataDrivenAnnotations(final TestClass testClass) {
        this(testClass, Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    DataDrivenAnnotations(final TestClass testClass, EnvironmentVariables environmentVariables) {
        this.testClass = testClass;
        this.environmentVariables = environmentVariables;
    }

    DataDrivenAnnotations usingEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new DataDrivenAnnotations(this.testClass, environmentVariables);
    }

    public DataTable getParametersTableFromTestDataSource() throws Throwable {
        TestDataSource testDataSource = new CSVTestDataSource(findTestDataSource(), findTestDataSeparator());
        List<Map<String, String>> testData = testDataSource.getData();
        List<String> headers = testDataSource.getHeaders();
        return DataTable.withHeaders(headers)
                .andMappedRows(testData)
                .build();
    }

    public DataTable getParametersTableFromTestDataAnnotation() {
        Method testDataMethod;
        String columnNamesString;
        List parametersList;

        try {
            testDataMethod = getTestDataMethod().getMethod();
            columnNamesString = testDataMethod.getAnnotation(TestData.class).columnNames();
            parametersList = (List) testDataMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Could not obtain test data from the test class", e);
        }

        return createParametersTableFrom(columnNamesString, convert(parametersList, toListOfObjects()));
    }

    private Converter<Object[], List<Object>> toListOfObjects() {
        return new Converter<Object[], List<Object>>() {

            public List<Object> convert(Object[] parameters) {
                return Arrays.asList(parameters);
            }
        };
    }

    private DataTable createParametersTableFrom(String columnNamesString, List<List<Object>> parametersList) {
        int numberOfColumns = parametersList.isEmpty() ? 0 : parametersList.get(0).size();
        List<String> columnNames = split(columnNamesString, numberOfColumns);
        return DataTable.withHeaders(columnNames)
                .andRows(parametersList)
                .build();
    }


    private List<String> split(String columnNamesString, int numberOfColumns) {

        if (StringUtils.isEmpty(columnNamesString)) {
            return numberedColumnHeadings(numberOfColumns);
        }

        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(columnNamesString);
    }

    private List<String> numberedColumnHeadings(int numberOfColumns) {
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < numberOfColumns; i++) {
            columnNames.add("Parameter " + (i + 1));
        }
        return columnNames;
    }

    public FrameworkMethod getTestDataMethod() throws Exception {
        FrameworkMethod method = findTestDataMethod();
        if (method == null) {
            throw new IllegalArgumentException("No public static @FilePathParser method on class "
                    + testClass.getName());
        }
        return method;
    }

    private FrameworkMethod findTestDataMethod() {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(TestData.class);
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }
        return null;
    }

    @SuppressWarnings("MalformedRegex")
    protected List<String> findTestDataSource() {
        String paths = findTestDataSourcePaths();
        List<String> validPaths = Lists.newArrayList();
        for (String path : Splitter.on(DATASOURCE_PATH_SEPARATORS).split(paths)) {
            if (CSVTestDataSource.validTestDataPath(path)) {
                validPaths.add(path);
            }
        }
        if (validPaths.isEmpty()) {
            throw new IllegalArgumentException("No test data file found for path: " + paths);
        }

        return validPaths;
    }

    protected String findTestDataSourcePaths() {
        return new FilePathParser(environmentVariables).getInstanciatedPath(findUseTestDataFromAnnotation().value());
    }

    private UseTestDataFrom findUseTestDataFromAnnotation() {
        return testClass.getJavaClass().getAnnotation(UseTestDataFrom.class);
    }

    public boolean hasTestDataDefined() {
        return (findTestDataMethod() != null);
    }

    public boolean hasTestDataSourceDefined() {
        return (findUseTestDataFromAnnotation() != null) && (findTestDataSource() != null);
    }

    public <T> List<T> getDataAsInstancesOf(final Class<T> clazz) throws IOException {
        TestDataSource testdata = new CSVTestDataSource(findTestDataSource(), findTestDataSeparator());
        return testdata.getDataAsInstancesOf(clazz);
    }

    public int countDataEntries() throws IOException {
        TestDataSource testdata = new CSVTestDataSource(findTestDataSource(), findTestDataSeparator());
        return testdata.getData().size();
    }

    private char findTestDataSeparator() {
        return findUseTestDataFromAnnotation().separator();
    }


}

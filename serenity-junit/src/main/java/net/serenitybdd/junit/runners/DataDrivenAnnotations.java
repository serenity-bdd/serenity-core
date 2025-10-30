package net.serenitybdd.junit.runners;

import com.google.common.base.Splitter;
import net.thucydides.core.steps.stepdata.CSVTestDataSource;
import net.thucydides.core.steps.stepdata.TestDataSource;
import net.thucydides.junit.annotations.TestData;
import net.thucydides.junit.annotations.UseTestDataFrom;
import net.thucydides.model.configuration.FilePathParser;
import net.thucydides.model.domain.DataTable;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataDrivenAnnotations {

    private final EnvironmentVariables environmentVariables;

    private final Pattern DATASOURCE_PATH_SEPARATORS = Pattern.compile("[;,]");

    public static DataDrivenAnnotations forClass(final Class testClass) {
        return new DataDrivenAnnotations(testClass);
    }

    private final Class testClass;

    DataDrivenAnnotations(final Class testClass) {
        this(testClass, SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    DataDrivenAnnotations(final Class testClass, EnvironmentVariables environmentVariables) {
        this.testClass = testClass;
        this.environmentVariables = environmentVariables;
    }

    public DataDrivenAnnotations usingEnvironmentVariables(EnvironmentVariables environmentVariables) {
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

    public List<Method> getTestMethods() {
        List<Method> methods = getAnnotatedMethods();
        if (methods.isEmpty()) {
            throw new IllegalStateException("Parameterized test should have at least one @Test method");
        }
        return methods;
    }

    public DataTable getParametersTableFromTestDataAnnotation() {
        Method testDataMethod;
        String columnNamesString;
        List parametersList;

        try {
            testDataMethod = getTestDataMethod();
            columnNamesString = testDataMethod.getAnnotation(TestData.class).columnNames();
            parametersList = (List) testDataMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Could not obtain test data from the test class", e);
        }

        List<List<Object>> parametersAsListsOfObjects = new ArrayList<>();
        for(Object parameterList : parametersList) {
            parametersAsListsOfObjects.add(listOfObjectsFrom((Object[]) parameterList));
        }

        return createParametersTableFrom(columnNamesString, parametersAsListsOfObjects);
    }


    private List<Object> listOfObjectsFrom(Object[] parameters) { return Arrays.asList(parameters); }

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

    public Method getTestDataMethod() throws Exception {
        Method method = findTestDataMethod();
        if (method == null) {
            throw new IllegalArgumentException("No public static @FilePathParser method on class "
                    + testClass.getName());
        }
        return method;
    }

    private Method findTestDataMethod() {
        List<Method> methods = getAnnotatedMethods(TestData.class);
        for (Method each : methods) {
            int modifiers = each.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }
        return null;
    }

    @SuppressWarnings("MalformedRegex")
    protected List<String> findTestDataSource() {
        String paths = findTestDataSourcePaths();
        List<String> validPaths = new ArrayList<>();
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
        return (UseTestDataFrom) testClass.getAnnotation(UseTestDataFrom.class);
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

    private List<Method> getAnnotatedMethods(){
        return Arrays.stream(testClass.getDeclaredMethods()).filter((Method method) ->{
            return method.getDeclaredAnnotations() != null;
        }).collect(Collectors.toList());
    }

    private List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass){
        return Arrays.stream(testClass.getDeclaredMethods()).filter((Method method) ->{
            return method.getAnnotation(annotationClass) != null;
        }).collect(Collectors.toList());
    }

}

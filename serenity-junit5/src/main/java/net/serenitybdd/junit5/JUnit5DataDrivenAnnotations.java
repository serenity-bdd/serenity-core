package net.serenitybdd.junit5;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Splitter;
import net.serenitybdd.junit5.datadriven.JUnit5CSVTestDataSource;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class JUnit5DataDrivenAnnotations {


    private final Logger logger = LoggerFactory.getLogger(JUnit5DataDrivenAnnotations.class);

    private final EnvironmentVariables environmentVariables;

    private final Class testClass;

    public static JUnit5DataDrivenAnnotations forClass(final Class testClass) {
        return new JUnit5DataDrivenAnnotations(testClass);
    }

    JUnit5DataDrivenAnnotations(final Class testClass) {
        this(testClass, Injectors.getInjector().getProvider(EnvironmentVariables.class).get());
    }

    JUnit5DataDrivenAnnotations(final Class testClass, EnvironmentVariables environmentVariables) {
        this.testClass = testClass;
        this.environmentVariables = environmentVariables;
    }

    public Map<String,DataTable> getParameterTables() {
        //String columnNamesString;
        //List<List<Object>> parametersList;
        List<Method> allMethods = findTestDataMethods();
        Map<String,DataTable> dataTables = new HashMap<>();
        for(Method testDataMethod : allMethods) {
            if(isAValueSourceAnnotatedMethod(testDataMethod)) {
                String columnNamesString = createColumnNamesFromParameterNames(testDataMethod);
                List<List<Object>> parametersList = listOfObjectsFrom(testDataMethod);
                List<List<Object>> parametersAsListsOfObjects = new ArrayList<>();
                for (List<Object> parameterList : parametersList) {
                    parametersAsListsOfObjects.add(parameterList);
                }
                String dataTableName = testClass.getCanonicalName() + "." + testDataMethod.getName();
                logger.info("GetParameterTables: Put parameter dataTableName " + dataTableName + " -- " + parametersAsListsOfObjects);
                dataTables.put(dataTableName, createParametersTableFrom(columnNamesString, parametersAsListsOfObjects));
            }
            else if(isACsvFileSourceAnnotatedMethod(testDataMethod))
            {
                CsvFileSource annotation = testDataMethod.getAnnotation(CsvFileSource.class);
                String columnNamesString = createColumnNamesFromParameterNames(testDataMethod);
                String dataTableName = testClass.getCanonicalName() + "." + testDataMethod.getName();
                try {
                    JUnit5CSVTestDataSource csvTestDataSource = new JUnit5CSVTestDataSource(Arrays.asList(annotation.resources()), CSVReader.DEFAULT_SEPARATOR);
                    List<Map<String, String>> data = csvTestDataSource.getData();
                    List<List<Object>> rows  = new ArrayList<>();
                    for(Map<String,String> dataRowMap : data)
                    {
                        ArrayList<Object> dataRow = new ArrayList<>();
                        for(String header : csvTestDataSource.getHeaders()) {
                            dataRow.add(dataRowMap.get(header));
                        }
                        rows.add(dataRow);
                    }
                    logger.info("GetParameterTablesCSV: Put parameter dataTableName " + dataTableName);
                    dataTables.put(dataTableName, createParametersTableFrom(columnNamesString,rows));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataTables;
    }

    private String createColumnNamesFromParameterNames(Method method) {
        StringBuffer columnNames = new StringBuffer();
        Parameter[] parameters = method.getParameters();
        int count = 0;
        for(Parameter parameter :  parameters) {
            columnNames.append(parameter.getName());
            if(count != (parameters.length - 1))
                columnNames.append(",");
            count++;
        }
        return columnNames.toString();
    }

    private List<List<Object>> listOfObjectsFrom(Method testDataMethod){
        ValueSource annotation = testDataMethod.getAnnotation(ValueSource.class);
        if(annotation.strings() != null && annotation.strings().length > 0)
            return listOfObjectsFrom(annotation.strings());
        else if(annotation.bytes() != null && annotation.bytes().length > 0)
            return listOfObjectsFrom(ArrayUtils.toObject(annotation.bytes()));
        else if(annotation.chars() != null && annotation.chars().length > 0)
            return listOfObjectsFrom(ArrayUtils.toObject(annotation.chars()));
        else if(annotation.doubles() != null && annotation.doubles().length > 0)
            return listOfObjectsFrom(ArrayUtils.toObject(annotation.doubles()));
        else if(annotation.floats() != null && annotation.floats().length > 0 )
            return listOfObjectsFrom(ArrayUtils.toObject(annotation.floats()));
        else if(annotation.ints() != null && annotation.ints().length > 0) {
            System.out.println("Annotation.ints found");
            return listOfObjectsFrom(ArrayUtils.toObject(annotation.ints()));
        }
        else if(annotation.shorts() != null && annotation.shorts().length > 0)
            return listOfObjectsFrom(ArrayUtils.toObject(annotation.shorts()));
        else if(annotation.classes() != null && annotation.classes().length > 0)
            return listOfObjectsFrom(annotation.classes());
        return null;
    }

    private List<List<Object>> listOfObjectsFrom(Object[] parameters) {
        List<List<Object>> retList = new ArrayList<>();
        for(Object parameter : parameters) {
            ArrayList parameterList =  new ArrayList();
            parameterList.add(parameter);
            retList.add(parameterList);
        }
        return retList;
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

    private List<Method> findTestDataMethods() {
        List<Method> methods = Arrays.asList(testClass.getMethods());
        return methods.stream().filter(this::findParameterizedTests).collect(Collectors.toList());
    }

    private boolean findParameterizedTests(Method method) {
        return method.getAnnotation(ParameterizedTest.class) != null &&
                (isAValueSourceAnnotatedMethod(method)
                        || isACsvFileSourceAnnotatedMethod(method)
                        || isACsvSourceAnnotatedMethod(method));
    }

    private boolean isAValueSourceAnnotatedMethod(Method method){
        return method.getAnnotation(ValueSource.class) != null;
    }

    private boolean isACsvFileSourceAnnotatedMethod(Method method){
        return method.getAnnotation(CsvFileSource.class) != null;
    }

    private boolean isACsvSourceAnnotatedMethod(Method method){
        return method.getAnnotation(CsvSource.class) != null;
    }
}

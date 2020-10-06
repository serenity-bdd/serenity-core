package net.serenitybdd.junit5;

import com.google.common.base.Splitter;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.DataTable;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        String columnNamesString;
        List<List<Object>> parametersList;
        List<Method> allMethods = findTestDataMethods();
        Map<String,DataTable> dataTables = new HashMap<>();
        for(Method testDataMethod : allMethods) {
            try {
                columnNamesString = createColumnNamesFromParameterNames(testDataMethod);
                parametersList = listOfObjectsFrom(testDataMethod);
            } catch (Exception e) {
                throw new RuntimeException("Could not obtain test data from the test class", e);
            }
            List<List<Object>> parametersAsListsOfObjects = new ArrayList<>();
            for (List<Object> parameterList : parametersList) {
                parametersAsListsOfObjects.add(parameterList);
            }
            String dataTableName = testClass.getCanonicalName() + "." + testDataMethod.getName();
            logger.info("GetParameterTables: Put parameter dataTableName " + dataTableName + " " + parametersAsListsOfObjects);
            dataTables.put(dataTableName,createParametersTableFrom(columnNamesString, parametersAsListsOfObjects));
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
                method.getAnnotation(ValueSource.class) != null;
    }
}

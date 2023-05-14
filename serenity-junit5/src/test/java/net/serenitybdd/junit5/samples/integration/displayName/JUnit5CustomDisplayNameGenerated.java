package net.serenitybdd.junit5.samples.integration.displayName;

import org.junit.jupiter.api.DisplayNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class JUnit5CustomDisplayNameGenerated extends DisplayNameGenerator.Standard {
    private static final Logger logger = LoggerFactory.getLogger(JUnit5CustomDisplayNameGenerated.class);

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return replaceCamelCase(super.generateDisplayNameForClass(testClass));
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return replaceCamelCase(super.generateDisplayNameForNestedClass(nestedClass));
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {

        StringBuilder result = new StringBuilder();
        result.append("epic_id :" + "1111" + ";");
        result.append("story_id:" + "2222" + ";");
        result.append("task_id:" + "3423" + ";");
        result.append("subtask_id:" + "343432" + ";");
        result.append("testcase_id:" + "234234234" + ";");
        result.append("testcase_version:" + "5.0" + ";");
        return result.toString();

    }

    String replaceCamelCase(String camelCase) {
        StringBuilder result = new StringBuilder();
        result.append(camelCase.charAt(0));
        for (int i = 1; i < camelCase.length(); i++) {
            if (Character.isUpperCase(camelCase.charAt(i))) {
                result.append(' ');
                result.append(Character.toLowerCase(camelCase.charAt(i)));
            } else {
                result.append(camelCase.charAt(i));
            }
        }
        return result.toString();
    }
}
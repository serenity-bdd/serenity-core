package net.thucydides.model.requirements.classpath;

import net.serenitybdd.annotations.TestAnnotations;
import net.thucydides.model.domain.TestTag;

import java.util.*;
import java.util.stream.Collectors;

public class AnnotatedTags {

    public static List<TestTag> forClassDefinedInPath(String path) {

        try {
            Class<?> testClass = AnnotatedTags.class.getClassLoader().loadClass(path);
            return TestAnnotations.forClass(testClass).getClassTags();
        } catch (ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static Map<String, Collection<TestTag>> forTestMethodsDefinedInPath(String path) {

        try {
            Class<?> testClass = AnnotatedTags.class.getClassLoader().loadClass(path);
            List<String> methodNames = TestAnnotations.forClass(testClass).getTestMethodNames();
            return methodNames.stream().collect(
                    Collectors.toMap(
                            methodName -> methodName,
                            methodName -> TestAnnotations.forClass(testClass).getTagsForMethod(methodName))
            );
        } catch (ClassNotFoundException e) {
            return new HashMap<>();
        }
    }


}

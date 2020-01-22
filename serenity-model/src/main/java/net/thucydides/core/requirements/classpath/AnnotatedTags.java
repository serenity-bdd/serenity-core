package net.thucydides.core.requirements.classpath;

import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.model.TestTag;

import java.util.ArrayList;
import java.util.List;

public class AnnotatedTags {

    public static List<TestTag> definedInPath(String path) {

        try {
            Class<?> testClass = AnnotatedTags.class.getClassLoader().loadClass(path);
            return TestAnnotations.forClass(testClass).getAllTags();
        } catch (ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}

package net.serenitybdd.annotations;

import net.thucydides.model.domain.TestTag;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads string-based {@link Epic}, {@link Feature}, and {@link Story} annotations
 * from classes and methods, producing {@link TestTag} objects.
 */
public class EpicFeatureStoryAnnotations {

    private EpicFeatureStoryAnnotations() {}

    /**
     * Reads @Epic, @Feature (string-valued), and @Story (string-valued) from a single annotated element.
     * Does not walk superclasses or declaring classes — use {@link #forClass} or {@link #forMethod} for that.
     */
    public static List<TestTag> tagsFromElement(AnnotatedElement element) {
        List<TestTag> tags = new ArrayList<>();

        Epic epic = element.getAnnotation(Epic.class);
        if (epic != null) {
            tags.add(TestTag.withName(epic.value()).andType("epic"));
        }

        Feature feature = element.getAnnotation(Feature.class);
        if (feature != null && !feature.value().isEmpty()) {
            tags.add(TestTag.withName(feature.value()).andType("feature"));
        }

        Story story = element.getAnnotation(Story.class);
        if (story != null && !story.value().isEmpty()) {
            tags.add(TestTag.withName(story.value()).andType("story"));
        }

        return tags;
    }

    /**
     * Reads annotations from the class, its superclass chain, and its enclosing class chain
     * (up to but not including Object). This ensures annotations on outer classes are inherited
     * by JUnit 5 {@code @Nested} inner classes.
     */
    public static List<TestTag> forClass(Class<?> testClass) {
        List<TestTag> tags = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>();
        Class<?> classToScan = testClass;
        while (classToScan != null) {
            Class<?> current = classToScan;
            while (current != null && current != Object.class) {
                if (visited.add(current)) {
                    tags.addAll(tagsFromElement(current));
                }
                current = current.getSuperclass();
            }
            classToScan = classToScan.getEnclosingClass();
        }
        return tags;
    }

    /**
     * Reads annotations from a method, then from its declaring class chain.
     */
    public static List<TestTag> forMethod(Method method) {
        List<TestTag> tags = new ArrayList<>();
        tags.addAll(tagsFromElement(method));
        tags.addAll(forClass(method.getDeclaringClass()));
        return tags;
    }
}
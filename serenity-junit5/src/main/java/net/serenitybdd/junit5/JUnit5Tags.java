package net.serenitybdd.junit5;

import net.serenitybdd.annotations.EpicFeatureStoryAnnotations;
import net.serenitybdd.annotations.SingleBrowser;
import net.thucydides.model.domain.TestTag;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class JUnit5Tags {
    private static final List<Class<? extends Annotation>> SERENITY_SPECIFIC_ANNOTATIONS = List.of(
            SingleBrowser.class
    );

    private static final Collection<? extends TestTag> NO_TAGS = new ArrayList<>();

    public static List<TestTag> forMethod(Method method) {
        List<TestTag> tags = new ArrayList<>();
        tags.addAll(singleTagAnnotationsIn(method));
        tags.addAll(multipleTagAnnotationsIn(method));
        tags.addAll(customAnnotationsAsTagsIn(method));
        tags.addAll(singleClassLevelTagAnnotationsIn(method.getDeclaringClass()));
        tags.addAll(multipleClassLevelTagAnnotationsIn(method.getDeclaringClass()));
        tags.addAll(customAnnotationsAsClassLevelTagsIn(method.getDeclaringClass()));
        tags.addAll(EpicFeatureStoryAnnotations.forMethod(method));
        return tags;
    }

    private static Collection<? extends TestTag> customAnnotationsAsClassLevelTagsIn(Class<?> declaringClass) {
        List<TestTag> tags = new ArrayList<>();
        while(declaringClass != null) {
            for (Class<? extends Annotation> serenitySpecificAnnotation : SERENITY_SPECIFIC_ANNOTATIONS) {
                if (declaringClass.getAnnotation(serenitySpecificAnnotation) != null) {
                    tags.add(testTagFromAnnotation(serenitySpecificAnnotation));
                }
            }
            declaringClass = declaringClass.getEnclosingClass();
        }
        return tags;
    }

    private static TestTag testTagFromAnnotation(Class<? extends Annotation> serenitySpecificAnnotation) {
        return TestTag.withValue(serenitySpecificAnnotation.getSimpleName().toLowerCase());
    }

    private static Collection<? extends TestTag> customAnnotationsAsTagsIn(Method method) {
        List<TestTag> tags = new ArrayList<>();
        for(Class<? extends Annotation> serenitySpecificAnnotation : SERENITY_SPECIFIC_ANNOTATIONS) {
            if (method.getAnnotation(serenitySpecificAnnotation) != null) {
                tags.add(testTagFromAnnotation(serenitySpecificAnnotation));
            }
        }
        return tags;
    }

    private static Collection<? extends TestTag> singleClassLevelTagAnnotationsIn(Class<?> declaringClass) {
        List<TestTag> classLevelTestTags = new ArrayList<>();

        while(declaringClass != null) {
            if (declaringClass.getAnnotation(Tag.class) != null) {
                Tag tag = declaringClass.getAnnotation(Tag.class);
                classLevelTestTags.addAll(junit5TagsAsSerenityTag(tag));
            }
            declaringClass = declaringClass.getEnclosingClass();
        }
        return classLevelTestTags;
    }

    private static Collection<? extends TestTag> multipleClassLevelTagAnnotationsIn(Class<?> declaringClass) {
        List<TestTag> classLevelTestTags = new ArrayList<>();

        while(declaringClass != null) {
            if (declaringClass.getAnnotation(Tags.class) != null) {
                Tag[] tags = declaringClass.getAnnotation(Tags.class).value();
                classLevelTestTags.addAll(junit5TagsAsSerenityTags(tags));
            }
            declaringClass = declaringClass.getEnclosingClass();
        }
        return classLevelTestTags;
    }

    private static Collection<? extends TestTag> multipleTagAnnotationsIn(Method method) {
        if (method.getAnnotation(Tags.class) != null) {
            Tag[] tags = method.getAnnotation(Tags.class).value();
            return junit5TagsAsSerenityTags(tags);
        }
        return NO_TAGS;
    }

    private static Collection<? extends TestTag> singleTagAnnotationsIn(Method method) {
        if (method.getAnnotation(Tag.class) != null) {
            Tag tag = method.getAnnotation(Tag.class);
            return junit5TagsAsSerenityTag(tag);
        }
        return NO_TAGS;
    }

    private static List<TestTag> junit5TagsAsSerenityTag(Tag tag) {
        return Collections.singletonList(TestTag.withValue(tag.value()));
    }

    private static List<TestTag> junit5TagsAsSerenityTags(Tag[] tags) {
        return Arrays.stream(tags)
                .map(tag -> TestTag.withValue(tag.value()))
                .collect(Collectors.toList());
    }
}

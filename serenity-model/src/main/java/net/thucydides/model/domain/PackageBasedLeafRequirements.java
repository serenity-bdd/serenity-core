package net.thucydides.model.domain;

import net.thucydides.model.requirements.PackageRequirementsTagProvider;
import net.thucydides.model.requirements.RequirementTypesProvider;
import net.thucydides.model.requirements.classpath.PathElements;
import net.thucydides.model.requirements.model.FeatureType;
import net.thucydides.model.util.EnvironmentVariables;

import java.lang.reflect.Method;

import static java.util.Arrays.stream;
import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_TEST_ROOT;

public class PackageBasedLeafRequirements {

    private final RequirementTypesProvider requirementTypesProvider;
    private final String rootPackage;

    public PackageBasedLeafRequirements(EnvironmentVariables environmentVariables) {
        this.rootPackage = SERENITY_TEST_ROOT.from(environmentVariables);
        this.requirementTypesProvider = new PackageRequirementsTagProvider(environmentVariables, rootPackage);
    }

    public Story testCase(Class<?> testCase) {

        String storyName = TestClassHierarchy.getInstance()
                                             .displayNameFor(testCase.getName())
                                             .orElse(testCase.getSimpleName());

        Story story;
        if (Story.testedInTestCase(testCase) != null) {
            story = Story.from(Story.testedInTestCase(testCase)).withType(typeFrom(testCase.getName()));
        } else if (Story.fromAnnotationsOn(testCase) != null) {
            story = Story.fromAnnotationsOn(testCase);
        } else if (containsJUnitTestCases(testCase)) {
//            story = Story.from(testCase).withType(FeatureType.STORY.toString())
            story = Story.from(testCase).withType(FeatureType.FEATURE.toString())
                    .withStoryName(storyName)
                    .withDisplayName(storyName);
        } else {
            story = Story.from(testCase).withType(typeFrom(testCase.getName()))
                    .withStoryName(storyName)
                    .withDisplayName(storyName);
        }
        return story;
    }

    private boolean containsJUnitTestCases(Class<?> testCase) {
        Method[] declaredMethods = testCase.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (stream(method.getAnnotations()).anyMatch(annotation -> annotation.annotationType().getName().endsWith(".Test"))) {
                return true;
            }
        }
        return false;
    }

    public Story story(Story userStory) {
        return userStory;
    }

    private String typeFrom(String path) {
        path = path.replaceAll("\\$", ".");
        if ((rootPackage == null) || requirementTypesProvider.getActiveRequirementTypes().isEmpty()) {
            return FeatureType.FEATURE.toString();
        }
        int requirementsLevel = PathElements.elementsOf(path, rootPackage).size() - 1;

        int effectiveRequirementsLevel = Math.min(requirementsLevel, requirementTypesProvider.getActiveRequirementTypes().size() - 1);

        return requirementTypesProvider.getActiveRequirementTypes().get(effectiveRequirementsLevel);
    }

}

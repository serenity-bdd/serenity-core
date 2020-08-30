package net.thucydides.core.model;

import net.thucydides.core.requirements.PackageRequirementsTagProvider;
import net.thucydides.core.requirements.RequirementTypesProvider;
import net.thucydides.core.requirements.classpath.PathElements;
import net.thucydides.core.requirements.model.FeatureType;
import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT;

public class PackageBasedLeafRequirements {

    private final RequirementTypesProvider requirementTypesProvider;
    private final String rootPackage;

    public PackageBasedLeafRequirements(EnvironmentVariables environmentVariables) {
        this.rootPackage = THUCYDIDES_TEST_ROOT.from(environmentVariables);
        this.requirementTypesProvider = new PackageRequirementsTagProvider(environmentVariables, rootPackage);
    }

    public Story testCase(Class<?> testCase) {

        Story story;
        if (Story.testedInTestCase(testCase) != null) {
            story = Story.from(Story.testedInTestCase(testCase)).withType(typeFrom(testCase.getName()));
        } else {
            story = Story.from(testCase).withType(typeFrom(testCase.getName()));
        }
        return story;
    }

    public Story story(Story userStory) {
        return userStory;
    }

    private String typeFrom(String path) {
        if ((rootPackage == null) || requirementTypesProvider.getActiveRequirementTypes().isEmpty()) {
            return FeatureType.STORY.toString();
        }
        int requirementsLevel = PathElements.elementsOf(path, rootPackage).size() - 1;

        int effectiveRequirementsLevel = Math.min(requirementsLevel, requirementTypesProvider.getActiveRequirementTypes().size() - 1);

        return requirementTypesProvider.getActiveRequirementTypes().get(effectiveRequirementsLevel);
    }

}

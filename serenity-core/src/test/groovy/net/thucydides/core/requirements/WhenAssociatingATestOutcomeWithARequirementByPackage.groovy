package net.thucydides.core.requirements

import annotatedstorieswithcontents.apples.TestSample1
import com.google.common.base.Optional
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.requirements.stories.grow_potatoes.ASampleTestWithACapability
import net.thucydides.core.requirements.stories.grow_potatoes.grow_new_potatoes.ASampleNestedTestWithACapability
import net.thucydides.core.requirements.stories.nocapacities.ASampleTestWithNoCapability
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenAssociatingATestOutcomeWithARequirementByPackage extends Specification {

    def setup() {
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider()
        capabilityProvider.clear()
    }

    def "Should read requirements structure from the package directory structure"() {
        given: "We define the root package in the 'thucydides.test.root' property"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load the requirements structure"
        List<Requirement> requirements = capabilityProvider.getRequirements()
        then:
        requirements
    }

    def "Should associate a test case to capability based on it's package"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleTestWithACapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [
                TestTag.withName("Grow potatoes").andType("capability"),
                TestTag.withName("Grow potatoes/A sample test with a capability").andType("story"),
        ] as Set
    }

    def "Should associate a nested test case to capability based on it's package"() {
        given: "We are using the package requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleNestedTestWithACapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [
                TestTag.withName("Grow potatoes").andType("capability"),
                TestTag.withName("Grow new potatoes/A sample nested test with a capability").andType("story"),
                TestTag.withName("Grow potatoes/Grow new potatoes").andType("feature")] as Set
    }

    def "Should not associate a test case if there is no matching capability"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleTestWithNoCapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [TestTag.withName("Nocapacities").andType("capability"),
                                                       TestTag.withName("Nocapacities/A sample test with no capability").andType("story")] as Set
    }

    def "Should find the direct parent requirement of a test outcome"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleTestWithACapability)
        then:
        capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
        capabilityProvider.getParentRequirementOf(testOutcome).get().name == "A sample test with a capability"

        PackageRequirementsTagProvider capabilityProvider2 = new PackageRequirementsTagProvider(vars);
        capabilityProvider2.getParentRequirementOf(testOutcome).isPresent()
        capabilityProvider2.getParentRequirementOf(testOutcome).get().name == "A sample test with a capability"

    }

    def "Should find the direct parent requirement of a test outcome from an annotated story"() {
        given: "We are using the annotated provider"
        def vars = new MockEnvironmentVariables()
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, "annotatedstorieswithcontents")
        RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load requirements we have an annotated test"
        def story = new Story(TestSample1.class)
        def testOutcome = new TestOutcome("Title for test 1", TestSample1.class, story)
        then:
        capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
        capabilityProvider.getParentRequirementOf(testOutcome).get().narrative.renderedText == "A Narrative for test 1\nMultiple lines"
    }

    def "Should find the direct parent requirement of a test outcome for nested requirements"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleNestedTestWithACapability)
        then:
        capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
        capabilityProvider.getParentRequirementOf(testOutcome).get().name == "A sample nested test with a capability"
    }

    def "Should associate a test case to a requirement based on its featurefile if provided"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleNestedTestWithACapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [TestTag.withName("Grow potatoes").andType("capability"),
                                                       TestTag.withName("Grow potatoes/Grow new potatoes").andType("feature"),
                                                       TestTag.withName("Grow new potatoes/A sample nested test with a capability").andType("story")] as Set
    }

    def "Should find the requirement for a given tag"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars);
        when: "We load requirements with nested capability directories and no .narrative files"
        def growPotatoesTag = TestTag.withName("Grow potatoes").andType("capability")
        then:
        Optional<Requirement> requirement = capabilityProvider.getRequirementFor(growPotatoesTag)
        requirement.get().getName() == "Grow potatoes"
        requirement.get().getType() == "capability"
        requirement.get().narrative.renderedText.contains "I want to grow potatoes"
    }


}


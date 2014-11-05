package net.thucydides.core.requirements

import annotatedstorieswithcontents.apples.TestSample1
import com.google.common.base.Optional
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.requirements.stories.grow_potatoes.ASampleTestWithACapability
import net.thucydides.core.requirements.stories.grow_potatoes.another_package.ASampleTestInAnotherPackage
import net.thucydides.core.requirements.stories.grow_potatoes.grow_new_potatoes.ASampleNestedTestWithACapability
import net.thucydides.core.requirements.stories.nocapacities.ASampleTestWithNoCapability
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenAssociatingATestOutcomeWithARequirement extends Specification {

    def "Should associate a test case to capability based on it's package"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = new TestOutcome("someTest",ASampleTestWithACapability)
        then:
            capabilityProvider.getTagsFor(testOutcome) == [TestTag.withName("Grow potatoes").andType("capability")] as Set
    }

    def "Should associate a nested test case to capability based on it's package"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = new TestOutcome("someTest",ASampleNestedTestWithACapability)
        then:
            capabilityProvider.getTagsFor(testOutcome) == [TestTag.withName("Grow potatoes").andType("capability"),
                                                           TestTag.withName("Grow potatoes/Grow new potatoes").andType("feature")]  as Set
    }

    def "Should associate a nested test case to the nearest above capacity"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and a narrative files"
            def testOutcome = new TestOutcome("someTest",ASampleTestInAnotherPackage)
        then:
            capabilityProvider.getTagsFor(testOutcome) == [TestTag.withName("Grow potatoes").andType("capability")]  as Set
    }

    def "Should not associate a test case if there is no matching capability"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = new TestOutcome("someTest",ASampleTestWithNoCapability)
        then:
            capabilityProvider.getTagsFor(testOutcome) == [] as Set
    }

    def "Should find the direct parent requirement of a test outcome"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = new TestOutcome("someTest",ASampleTestWithACapability)
        then:
            capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
            capabilityProvider.getParentRequirementOf(testOutcome).get().name == "Grow potatoes"
    }

    def "Should find the direct parent requirement of a test outcome from an annotated story"(){
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
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = new TestOutcome("someTest",ASampleNestedTestWithACapability)
        then:
            capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
            capabilityProvider.getParentRequirementOf(testOutcome).get().name == "Grow new potatoes"
    }

    def "Should find the direct parent requirement of a test outcome related to a story"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = TestOutcome.forTestInStory("someTest", Story.withIdAndPath("PlantPotatoes","Plant potatoes","grow_potatoes/grow_new_potatoes/PlantPotatoes.story"))
        then:
            capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
            capabilityProvider.getParentRequirementOf(testOutcome).get().name == "Plant potatoes"
    }

    def "Should find no direct parent requirement of a test outcome if none is defined"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = new TestOutcome("someTest",ASampleTestWithNoCapability)
        then:
            !capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
    }

    def "Should find the requirement for a given tag"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def growPotatoesTag = TestTag.withName("Grow potatoes").andType("capability")
        then:
            Optional<Requirement> requirement = capabilityProvider.getRequirementFor(growPotatoesTag)
            requirement.get().getName() == "Grow potatoes"
            requirement.get().getType() == "capability"
            requirement.get().narrative.renderedText.contains "I want to grow potatoes"
    }

    def "Should not find the requirement if there are no matching requirements for a tag"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def growPotatoesTag = TestTag.withName("Grow pink and purple potatoes").andType("capability")
        then:
            Optional<Requirement> requirement = capabilityProvider.getRequirementFor(growPotatoesTag)
            requirement.absent()
    }
}


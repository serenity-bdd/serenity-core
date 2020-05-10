package net.thucydides.core.requirements

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenAssociatingATestOutcomeWithARequirementInAFeatureFile extends Specification {


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
            capabilityProvider.getParentRequirementOf(testOutcome).get().name == "Plant Potatoes"
    }

    def "Should find the direct parent requirement of a test outcome related to a feature file without a path"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars);
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = TestOutcome.forTestInStory("someTest",
                                                      Story.withIdAndPath("PlantPotatoes","Plant potatoes","PlantPotatoes.story"))
        then:
            capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
            capabilityProvider.getParentRequirementOf(testOutcome).get().name == "Plant Potatoes"
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

    def "Should find the requirement for a feature tag"() {
        given: "We load requirements with nested capability directories"
            RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/feature_files");
            def testOutcome = new TestOutcome("someTest")
            testOutcome.addTags([TestTag.withName('Planting a new apple tree').andType('feature'),
                                 TestTag.withName('123').andType('issue'),
                                 TestTag.withName('Planting an apple tree').andType('story')])

        when:
            Optional<Requirement> requirement = capabilityProvider.getParentRequirementOf(testOutcome)
        then: "the nested requirements should be recorded as features"
            requirement.isPresent()
        and:
            requirement.get().featureFileName == "PlantingAnAppleTree.feature"

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
            !requirement.isPresent()
    }
}


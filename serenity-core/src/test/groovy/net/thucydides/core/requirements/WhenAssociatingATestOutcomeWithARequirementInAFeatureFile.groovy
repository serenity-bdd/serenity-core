package net.thucydides.core.requirements

import net.thucydides.model.domain.Story
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestTag
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider
import net.thucydides.model.requirements.model.Requirement
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenAssociatingATestOutcomeWithARequirementInAFeatureFile extends Specification {


    def "Should find the direct parent requirement of a test outcome related to a story"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables()
        FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars)
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = TestOutcome.forTestInStory("someTest", Story.withIdAndPath("PlantPotatoes","Plant potatoes","grow_potatoes/grow_new_potatoes/PlantPotatoes.story"))
        then:
            capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
            capabilityProvider.getParentRequirementOf(testOutcome).get().name == "PlantPotatoes"
            capabilityProvider.getParentRequirementOf(testOutcome).get().displayName == "Plant Potatoes"
    }

    def "Should not find the requirement if there are no matching requirements for a tag"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables()
            FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories", 0, vars)
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def growPotatoesTag = TestTag.withName("Grow pink and purple potatoes").andType("capability")
        then:
            Optional<Requirement> requirement = capabilityProvider.getRequirementFor(growPotatoesTag)
            !requirement.isPresent()
    }
}


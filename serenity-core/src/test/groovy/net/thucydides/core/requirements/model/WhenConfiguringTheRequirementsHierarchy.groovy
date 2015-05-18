package net.thucydides.core.requirements.model

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by john on 1/05/2015.
 */
class WhenConfiguringTheRequirementsHierarchy extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def "Default hierarchy should be capability, feature, story"() {
        given:
            def requirementsConfiguration = new RequirementsConfiguration(environmentVariables)
            requirementsConfiguration.rootRequirementsDirectory = "/no-feature-files-here"
        when:
            def requirementTypes = requirementsConfiguration.requirementTypes
        then:
            requirementTypes == ["capability","feature","story"]
    }

    def "Default hierarchy can be overriden with system properties"() {
        given:
            environmentVariables.setProperty("serenity.requirement.types","apples,oranges")
            def requirementsConfiguration = new RequirementsConfiguration(environmentVariables)
            requirementsConfiguration.rootRequirementsDirectory = "/no-feature-files-here"
        when:
            def requirementTypes = requirementsConfiguration.requirementTypes
        then:
            requirementTypes == ["apples","oranges"]
    }

    @Unroll
    def "If story or feature files are present a JBehave or Cucumber-compatible structure will be proposed"() {
        given:
            def requirementsConfiguration = new RequirementsConfiguration(environmentVariables)
            requirementsConfiguration.rootRequirementsDirectory = directory
        when:
            def requirementTypes = requirementsConfiguration.requirementTypes
        then:
            requirementTypes == expectedRequirementsTypes
        where:
            directory                | expectedRequirementsTypes
            "/one-level-story-files" | ["feature", "story"]
            "/two-level-story-files" | ["capability","feature", "story"]
            "/one-level-feature-files" | ["capability","feature"]
            "/two-level-feature-files" | ["theme","capability","feature"]
    }
}

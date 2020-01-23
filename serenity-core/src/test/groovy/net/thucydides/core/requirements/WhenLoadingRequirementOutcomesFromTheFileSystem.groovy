package net.thucydides.core.requirements

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenLoadingRequirementOutcomesFromTheFileSystem extends Specification {

    def "Should be able to load capabilities from the default directory structure"() {
        given: "We are using the default requirements service"
            Requirements requirements = new FileSystemRequirements(osSpecific("sample-story-directories/capabilities_and_features"))
        when: "We load the available requirements"
        def capabilities = requirements.requirementsService.requirements
        def capabilityNames = capabilities.collect { it.name }
        def capabilityTypes = capabilities.collect { it.type }
        then: "the requirements should be loaded from the first-level sub-directories"
        capabilityNames == ["Grow apples", "Grow potatoes", "Grow zuchinnis"]
        capabilityTypes == ["capability","theme","capability"]
    }

    String osSpecific(String path) {
        path.replaceAll("/",File.separator)
    }

    def "Should be able to load release versions with the capabilities from the default directory structure"() {
        given: "We are using the default requirements provider"
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/capabilities_and_features"));
        when: "We load the available requirements"
        def capabilities = capabilityProvider.getRequirements()
        then: "the requirements should have release versions if specified"
        capabilities[0].children[2].children[0].releaseVersions == ["Release 1", "Iteration 1.1"]
    }

    def "Should be able to load capabilities from a directory structure containing spaces"() {
        given: "We are using the default requirements provider"
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/capabilities_and_features with spaces"));
        when: "We load the available requirements"
        def capabilities = capabilityProvider.getRequirements()
        def capabilityNames = capabilities.collect { it.name }
        then: "the requirements should be loaded from the first-level sub-directories"
        capabilityNames == ["Grow apples", "Grow potatoes", "Grow zuchinnis"]
    }

    def "Should be able to load capabilities from a directory structure containing spaces in the path"() {
        given: "We are using the default requirements provider"
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories");
        when: "We load the available requirements"
        def capabilities = capabilityProvider.getRequirements()
        def capabilityNames = capabilities.collect { it.name }
        then: "the requirements should be loaded from the first-level sub-directories"
        capabilityNames == ["Grow cucumbers", "Grow potatoes", "Grow wheat", "Raise chickens"]
        and: "the child requirements should be found"
        def growCucumbers = capabilities.get(0)
        def cucumberFeatures = growCucumbers.children.collect { it.name }
        cucumberFeatures == ["Grow green cucumbers"]

    }

    def "Should be able to load issues from the default directory structure"() {
        given: "We are using the default requirements provider"
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/capabilities_and_features"));
        when: "We load the available requirements"
        def capabilities = capabilityProvider.getRequirements()
        def ids = capabilities.collect { it.cardNumber }
        then: "the card numbers should be read from the narratives if present "
        ids == ["#123", null, null]
    }

    def "Should derive title from directory name if not present in narrative"() {
        given: "there is a capability.narrativeText file in a directory that does not have a title line"
        def capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/capabilities_and_features"))
        when: "We try to load the capability from the directory"
        def capabilities = capabilityProvider.getRequirements()
        then: "the capability should be found"
        def potatoeGrowingCapability = capabilities.get(1)
        then: "the title should be a human-readable version of the directory name"
        potatoeGrowingCapability.name == "Grow potatoes"
    }

    def "Should support feature files in the requirements directory"() {
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/feature_files"));
        when: "We load requirements with nested capability directories"
        def capabilities = capabilityProvider.getRequirements()
        def growApples = capabilities.get(0)
        then: "the nested requirements should be recorded as features"
        growApples.type == "capability"
        and:
        growApples.childrenCount
        and:
        growApples.children[0].type == "feature"
    }

    def "Should be able to customize the requirements levels"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.requirement.types", "feature, story")
        and:
            RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/narrative_files"),0,environmentVariables);
        when: "We load requirements with nested capability directories"
            def capabilities = capabilityProvider.getRequirements()
            def types = capabilities.collect { it -> it.type }
        then: "the nested requirements should be recorded as features"
            types == ["feature","feature","feature"]
    }


    def "Should be able to read narrative files in directories with feature files"() {
        given:
        def environmentVariables = new MockEnvironmentVariables()
//        environmentVariables.setProperty("serenity.requirement.types", "feature, story")
        and:
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/simple-features"),0,environmentVariables);
        when: "We load requirements with a directory containing features and narratives"
        def capabilities = capabilityProvider.getRequirements()
        def types = capabilities.collect { it -> it.type }
        then: "the top level requirement should be recorded as a capability"
        types == ["capability"]
    }



    def "Should map features files in the requirements directory"() {
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/feature_files"));
        when: "We load requirements with nested capability directories"
        def capabilities = capabilityProvider.getRequirements()

        then: "the nested requirements should be recorded as features"
        def growApples = capabilities.get(0)
        and:
        growApples.childrenCount
        and:
        growApples.children[0].type == "feature"
    }


    def "The capability is determined by a configurable convention"() {
        given: "We are using the default requirements provider"
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/capabilities_and_features"));
        when: "We load the available requirements"
        def capabilities = capabilityProvider.getRequirements()
        then: "the requirements should be of type 'capability"
        capabilities.get(2).type == "capability"

    }

    def "capabilities can be nested"() {
        given: "We are using the default requirements provider"
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/capabilities_and_features"));
        when: "We load requirements with nested capability directories"
        def capabilities = capabilityProvider.getRequirements()
        then: "the nested requirements should be recorded"
        def growApples = capabilities.get(0)
        assert !growApples.children.isEmpty()
        and: "the capablity names are derived from the directory names unless a narrative.txt file is present"
        def capabilityNames = capabilities.get(0).children.collect { it.displayName }
        capabilityNames == ["Grow cider apples", "Grow granny smiths", "Grow shiny red apples"]
    }

    def "nested capability types are set by convention if no narrative.txt files are present"() {
        given: "We are using the default requirements provider"
        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/two-level-feature-files-with-no-narratives/features"));
        when: "We load requirements with nested requirement directories and no narrative.txt files"
        def capabilities = capabilityProvider.getRequirements()
        def theme = capabilities.get(0)
        def capabilityTypes = theme.children.collect { it.type }
        then: "the nested requirement are of type 'feature'"
        theme.type == "theme"
        capabilityTypes == ["capability", "capability"]
    }

    def "default nested requirement types can be overridden using an environment variable"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the requirement type hierarchy in the environment variables"
        vars.setProperty("serenity.requirement.types", "a, b, c")
        FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider(osSpecific("sample-story-directories/two-level-feature-files-with-no-narratives/features"), 0, vars);
        when: "We load requirements with nested requirement directories and no .narrative files"
        def capabilities = capabilityProvider.getRequirements()
        then: "the second-level requirement are of type 'epic'"
        capabilities.get(0).getType() == "a"
        capabilities.get(0).getChildren().get(0).getType() == "b"
        capabilities.get(0).getChildren().get(0).getChildren().get(0).getType() == "feature"
    }
}

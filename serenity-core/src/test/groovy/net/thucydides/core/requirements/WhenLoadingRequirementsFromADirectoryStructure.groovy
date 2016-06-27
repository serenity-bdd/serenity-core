package net.thucydides.core.requirements

import spock.lang.Specification

class WhenLoadingRequirementsFromADirectoryStructure extends Specification {

    def "Should be able to load capabilities from the default directory structure"() {
        given: "We are using the default requirements provider"
            RequirementsService requirementsService= new FileSystemRequirementsService("sample-story-directories/capabilities_and_features");
        when: "We load the available requirements"

            def capabilities = requirementsService.requirements;
            def capabilityNames = capabilities.collect { it.name }
            def capabilityTypes = capabilities.collect { it.type }
        then: "the requirements should be loaded from the first-level sub-directories"
            capabilityNames == ["Grow apples", "Grow potatoes", "Grow zuchinnis"]
            capabilityTypes == ["capability","theme","capability"]
    }

    def "Should be able to use a Requirements object to simplify requirements configuration"() {
        given: "We are using the default requirements provider"
            Requirements requirements = new FileSystemRequirements("sample-story-directories/capabilities_and_features")
        when: "We load the available requirements"

        def capabilities = requirements.requirementsService.requirements;
        def capabilityNames = capabilities.collect { it.name }
        def capabilityTypes = capabilities.collect { it.type }
        then: "the requirements should be loaded from the first-level sub-directories"
        capabilityNames == ["Grow apples", "Grow potatoes", "Grow zuchinnis"]
        capabilityTypes == ["capability","theme","capability"]
    }

//
//    def "Should be able to load release versions with the capabilities from the default directory structure"() {
//        given: "We are using the default requirements provider"
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features");
//        when: "We load the available requirements"
//        def capabilities = capabilityProvider.getRequirements()
//        def capabilityNames = capabilities.collect { it.name }
//        then: "the requirements should have release versions if specified"
//        capabilities[0].children[2].children[0].releaseVersions == ["Release 1", "Iteration 1.1"]
//    }
//
//    def "Should be able to load capabilities from a directory structure containing spaces"() {
//        given: "We are using the default requirements provider"
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features with spaces");
//        when: "We load the available requirements"
//        def capabilities = capabilityProvider.getRequirements()
//        def capabilityNames = capabilities.collect { it.name }
//        then: "the requirements should be loaded from the first-level sub-directories"
//        capabilityNames == ["Grow apples", "Grow potatoes", "Grow zuchinnis"]
//    }
//
//    def "Should be able to load capabilities from a directory structure containing spaces in the path"() {
//        given: "We are using the default requirements provider"
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("stories");
//        when: "We load the available requirements"
//        def capabilities = capabilityProvider.getRequirements()
//        def capabilityNames = capabilities.collect { it.name }
//        then: "the requirements should be loaded from the first-level sub-directories"
//        capabilityNames == ["Grow cucumbers", "Grow potatoes", "Grow wheat", "Raise chickens"]
//        and: "the child requirements should be found"
//        def growCucumbers = capabilities.get(0)
//        def cucumberFeatures = growCucumbers.children.collect { it.name }
//        cucumberFeatures == ["Grow green cucumbers"]
//
//    }
//
//    def "Should be able to load issues from the default directory structure"() {
//        given: "We are using the default requirements provider"
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features");
//        when: "We load the available requirements"
//        def capabilities = capabilityProvider.getRequirements()
//        def ids = capabilities.collect { it.cardNumber }
//        then: "the card numbers should be read from the narratives if present "
//        ids == ["#123", null, null]
//    }
//
//    def "Should derive title from directory name if not present in narrative"() {
//        given: "there is a capability.narrativeText file in a directory that does not have a title line"
//        def capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features")
//        when: "We try to load the capability from the directory"
//        def capabilities = capabilityProvider.getRequirements()
//        then: "the capability should be found"
//        def potatoeGrowingCapability = capabilities.get(1)
//        then: "the title should be a human-readable version of the directory name"
//        potatoeGrowingCapability.name == "Grow potatoes"
//    }
//
//    def "Should support feature files in the requirements directory"() {
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/feature_files");
//        when: "We load requirements with nested capability directories"
//        def capabilities = capabilityProvider.getRequirements()
//        def growApples = capabilities.get(0)
//        then: "the nested requirements should be recorded as features"
//        growApples.type == "capability"
//        and:
//        growApples.childrenCount
//        and:
//        growApples.children[0].type == "feature"
//    }
//
//    def "Should be able to customize the requirements levels"() {
//        given:
//            def environmentVariables = new MockEnvironmentVariables()
//            environmentVariables.setProperty("serenity.requirement.types", "feature, story")
//        and:
//            RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/narrative_files",0,environmentVariables);
//        when: "We load requirements with nested capability directories"
//            def capabilities = capabilityProvider.getRequirements()
//            def types = capabilities.collect { it -> it.type }
//        then: "the nested requirements should be recorded as features"
//            types == ["feature","feature","feature"]
//    }
//
//
//    def "Should map features files in the requirements directory"() {
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/feature_files");
//        when: "We load requirements with nested capability directories"
//        def capabilities = capabilityProvider.getRequirements()
//
//        then: "the nested requirements should be recorded as features"
//        def growApples = capabilities.get(0)
//        and:
//        growApples.childrenCount
//        and:
//        growApples.children[0].type == "feature"
//    }
//
//
//    def "The capability is determined by a configurable convention"() {
//        given: "We are using the default requirements provider"
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features");
//        when: "We load the available requirements"
//        def capabilities = capabilityProvider.getRequirements()
//        then: "the requirements should be of type 'capability"
//        capabilities.get(2).type == "capability"
//
//    }
//
//    def "capabilities can be nested"() {
//        given: "We are using the default requirements provider"
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features");
//        when: "We load requirements with nested capability directories"
//        def capabilities = capabilityProvider.getRequirements()
//        then: "the nested requirements should be recorded"
//        def growApples = capabilities.get(0)
//        assert !growApples.children.isEmpty()
//        and: "the capablity names are derived from the directory names"
//        def capabilityNames = capabilities.get(0).children.collect { it.name }
//        capabilityNames == ["Grow cider apples", "Grow granny smiths", "Grow red apples"]
//    }
//
//    def "nested capability types are set by convention if no narrative.txt files are present"() {
//        given: "We are using the default requirements provider"
//        RequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features");
//        when: "We load requirements with nested requirement directories and no narrative.txt files"
//        def capabilities = capabilityProvider.getRequirements()
//        then: "the nested requirement are of type 'feature'"
//        def capabilityTypes = capabilities.get(0).children.collect { it.type }
//        capabilityTypes == ["feature", "feature", "feature"]
//    }
//
//    def "default nested requirement types can be overriden using an environment variable"() {
//        given: "We are using the default requirements provider"
//        EnvironmentVariables vars = new MockEnvironmentVariables();
//        and: "We define the requirement type hierarchy in the environment variables"
//        vars.setProperty("thucydides.requirement.types", "theme, epic, feature")
//        FileSystemRequirementsTagProvider capabilityProvider = new FileSystemRequirementsTagProvider("sample-story-directories/capabilities_and_features", 0, vars);
//        when: "We load requirements with nested requirement directories and no .narrative files"
//        def capabilities = capabilityProvider.getRequirements()
//        then: "the second-level requirement are of type 'epic'"
//        capabilities.get(0).getType() == "theme"
//        capabilities.get(0).getChildren().get(0).getType() == "epic"
//        capabilities.get(0).getChildren().get(0).getChildren().get(0).getType() == "feature"
//        capabilities.get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getType() == "feature"
//    }
}

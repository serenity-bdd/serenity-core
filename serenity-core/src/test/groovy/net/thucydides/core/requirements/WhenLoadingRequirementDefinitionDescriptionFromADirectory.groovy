package net.thucydides.core.requirements

import net.thucydides.core.requirements.model.NarrativeReader
import net.thucydides.core.util.FileSystemUtils
import spock.lang.Specification

class WhenLoadingRequirementDefinitionDescriptionFromADirectory extends Specification {

    def "Directories in a 1-level directory structure represent the top level of the requirements hierarchy"() {
        given: "there is a readme.md file in a single-level directory structure"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/one-level-feature-files/features/fruit")
            Requirements requirements = new FileSystemRequirements("sample-story-directories/one-level-feature-files/features")
        when:
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/one-level-feature-files/features")
            def narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrative should be represented as a top-level requirement"
            narrative.present
            narrative.get().title.get() == "Planting Fruit"
            narrative.get().type == "capability"
        and: "both top level directories should represent top level requirements"
            requirements.requirementsService.requirements.size() == 2
            requirements.requirementsService.requirements[0].type == "capability"
            requirements.requirementsService.requirements[1].type == "capability"
    }


    def "Directories in a 1-level JBehave directory structure represent the top level of the requirements hierarchy"() {
        given: "there is a readme.md file in a single-level directory structure"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/one-level-story-files/stories/fruit")
            Requirements requirements = new FileSystemRequirements("sample-story-directories/one-level-story-files/stories")
        when:
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/one-level-story-files/stories")
            def narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrative should be represented as a top-level requirement"
            narrative.present
            narrative.get().title.get() == "Planting Fruit"
            narrative.get().type == "feature"
        and: "both top level directories should represent top level requirements"
            requirements.requirementsService.requirements.size() == 2
            requirements.requirementsService.requirements[0].type == "feature"
            requirements.requirementsService.requirements[1].type == "feature"
    }

    def "2nd level directories in a 2-level directory structure should be 2nd level requirements"() {
        given: "there is a readme.md file in a single-level directory structure"
        File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/two-level-feature-files/features/food/fruit")
        Requirements requirements = new FileSystemRequirements("sample-story-directories/two-level-feature-files/features")
        when:
        def reader = NarrativeReader.forRootDirectory("sample-story-directories/two-level-feature-files/features")
        def narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrative should be represented as a top-level requirement"
        narrative.present
        narrative.get().title.get() == "Planting Fruit"
        narrative.get().type == "capability"
    }

    def "Should be able to load a narrative description from a directory"() {
        given: "there is a capability.narrativeText file in a directory"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            def narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText title and description should be loaded"
            narrative.get().title.get() == "Grow more apples"
            narrative.get().text.contains("In order to make apple pies") &&
            narrative.get().text.contains("As a farmer") &&
            narrative.get().text.contains("I want to grow apples")
        and: "the narrative type should be derived from the directory"
            narrative.get().type == "capability"
    }

    def "Should be able to override the default capability type in the narrative file"() {
        given: "there is a capability.narrativeText file in a directory"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_potatoes")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            def narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrative type should be derived from the first line in the narrative"
            narrative.get().type == "theme"
    }


//    def "Should be able to derive the narrative type from the directory level"() {
//        given: "there is a narrative.txt file in a directory"
//            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples")
//        when: "We try to load the narrativeText from the directory"
//            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
//            def narrative = reader.loadFrom(directoryContainingANarrative)
//        then: "the narrativeText should be found"
//            narrative.present
//        and: "the narrativeText title and description should be loaded"
//            narrative.get().title.get() == "Grow red apples"
//        and: "the type should be derived from the directory level"
//            narrative.get().type == "capability"
//    }
//
    def "Should be able to cope with missing title lines"() {
        given: "there is a narrative.txt file in a directory"
            File reqDirectory = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_granny_smiths")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            def narrative = reader.loadFromStoryFile(new File(reqDirectory, "PlantingASmallAppleTree.story"))
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText title should not be loaded"
            !narrative.get().title.isPresent()
    }

    def "Should read narrative from a feature file"() {
        given: "there is a narrative.txt file in a directory"
        File reqDirectory = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples/grow_special_red_apples")
        when: "We try to load a feature file"
        def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
        def narrative = reader.loadFromStoryFile(new File(reqDirectory, "PlantingADifferentAppleTree.feature"))
        then: "the narrativeText should be found"
        narrative.present
        narrative.get().title.get() == 'Planting a new apple tree'
        narrative.get().text.replace("\r\n", "\n").replace("\r","\n") == "  As a farmer\n  I want to plant an apple tree\n  So that I can grow apples\n\n  This is really important"
    }


    def "Should read narrative in a comment in a feature file"() {
        given: "there is a narrative.txt file in a directory"
        File reqDirectory = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples/grow_special_red_apples")
        when: "We try to load a feature file"
        def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
        def narrative = reader.loadFromStoryFile(new File(reqDirectory, "PlantingAnAppleTreeWithAComment.feature"))
        then: "the narrativeText should be found"
        narrative.present
        narrative.get().title.get() == 'Planting a new apple tree'
        narrative.get().text.replace("\r\n", "\n").replace("\r","\n") == "  As a farmer\n  I want to plant an apple tree\n  So that I can grow apples\n\n  This is really important"
    }
    def "Should use the lowest requirement type for deeply nested requirements"() {
        given: "there is a narrative.txt file in a directory"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples/grow_special_red_apples")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            def narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText title and description should be loaded"
            narrative.get().title.get() == "Grow special red apples"
        and: "the type should be derived from lowest requirement type"
            narrative.get().type == "story"
    }

    def "Should read release version numbers if provided"() {
        given: "there is a narrative.txt file in a directory"
            File directoryContainingANarrative = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples/grow_special_red_apples")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            def narrative = reader.loadFrom(directoryContainingANarrative)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrativeText should contain release version numbers"
            narrative.get().versionNumbers == ["Release 1", "Iteration 1.1"]
    }

    def "Should ignore GivenStories clause in requirements from .story files"() {
        given: "there is a .story file in a directory"
            File storyFile = directoryInClasspathAt("sample-story-directories/capabilities_and_features/grow_apples/grow_red_apples/grow_special_red_apples/PlantingAnotherAppleTree.story")
        when: "We try to load the narrativeText from the directory"
            def reader = NarrativeReader.forRootDirectory("sample-story-directories/capabilities_and_features")
            def narrative = reader.loadFromStoryFile(storyFile)
        then: "the narrativeText should be found"
            narrative.present
        and: "the narrative text should be loaded"
            !narrative.get().title.isPresent()
            narrative.get().getText().contains("As a farmer")
            narrative.get().getText().contains("I want to plant an apple tree")
            narrative.get().getText().contains("So that I can grow apples")
        and: "the type should be story"
            narrative.get().type == "story"
        and: "meta data is skipped"
            !narrative.get().getText().contains("Meta") && !narrative.get().getText().contains("issue")
    }

    File directoryInClasspathAt(String path) {
        return FileSystemUtils.getResourceAsFile(path);
    }
}

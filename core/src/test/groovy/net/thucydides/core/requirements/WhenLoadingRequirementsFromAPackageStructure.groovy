package net.thucydides.core.requirements

import com.google.common.base.Optional
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenLoadingRequirementsFromAPackageStructure extends Specification {
    public static final String ROOT_DIRECTORY = "annotatedstorieswithcontents"

    def "Should be able to load capabilities from the default package structure"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements();
            def capabilityNames = capabilities.collect {it.name}
            def capabilitiyTexts = capabilities.collect {it.narrative.renderedText}
        then:
            capabilityNames == ["Apples", "Nice zucchinis", "Potatoes"]
            capabilitiyTexts == ["This is a narrative\nFor apples", "This is a narrative\nFor NiceZuchinnis",
                    "This is a narrative\nFor a potato"]
    }

    def "Should be able to load issues from the default directory structure"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements()
            def ids = capabilities.collect {it.cardNumber}
        then: "the card numbers should be read from the narratives if present"
            ids == ["#123", "", ""]
    }

    def "Should derive title from the directory name if not present in narrative"(){
        given: "there is a Narrative annotation on a pacakge that does not have a title line"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We try to load the capability from the package"
            def capabilities = capabilityProvider.getRequirements()
        then: "the capability should be found"
            def zucchiniCapability = capabilities.get(1)
        then: "the title should be a human-readable version of the directory name"
            zucchiniCapability.name == "Nice zucchinis"
    }

    def "Should find general requirement for a given tag"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
            def growApplesTag = TestTag.withName("Apples").andType("capability")
        then:
            Optional<Requirement> requirement = capabilityProvider.getRequirementFor(growApplesTag)
            requirement.get().getName() == "Apples"
            requirement.get().getType() == "capability"
    }


    def "Should find specific requirement for a given tag"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
        RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
        def growApplesTag = TestTag.withName("Apples").andType("capability")
        then:
        Optional<Requirement> requirement = capabilityProvider.getRequirementFor(growApplesTag)
        requirement.get().getName() == "Apples"
        requirement.get().getType() == "capability"
    }

    def "Should not find requirement for a given tag with a different type"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
        RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("thucydides.test.root","net.thucydides.core.requirements.stories")
        when: "We load requirements with nested capability directories and no .narrative files"
        def growApplesTag = TestTag.withName("Apples").andType("epic")
        then:
        !capabilityProvider.getRequirementFor(growApplesTag).isPresent()
    }

    def "capabilities can be nested"(){
        given: "We are using the default requirements provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load requirements with nested capabilities"
            def capabilities = capabilityProvider.getRequirements()
        then: "the nested requirements should be recorded"
            def capabilityNames = capabilities.get(2).children.collect {it.name}
            //we've got both a narrative from a test and from a directory
            capabilityNames == ["Test2", "Big potatoes"]
    }

    def "nested capability types are set by convention if no Narrative annotation are present"() {
        given: "We are using default requirement package"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load requirements with nested requirement packages and no Narrative annotations"
            def capabilities = capabilityProvider.getRequirements()
        then: "The nested requirement are of type 'feature'"
            def capabilityTypes = capabilities.get(2).children.collect{it.type}
            capabilities.collect{it.type} == ["capability", "capability", "mytype"]
            capabilityTypes == ["story", "feature"]
    }

    def "default nested requirement types can be overriden using an environment variable"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables()
        and: "We define the requirement type hierarchy in the environment variables"
            vars.setProperty("thucydides.requirement.types","theme, epic, feature")
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
        RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load requirements with nested requirement pacakge"
            def capabilities = capabilityProvider.getRequirements()
        then: "the second-level requirement are of type 'epic'"
            capabilities.collect{it.type} == ["theme", "theme", "mytype"]
            capabilities.get(2).children.collect{it.type} == ["story", "epic"]
    }

    def "default requirement directory can be overriden using an environment variable"() {
        given: "We using the annotated requirement provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the requirement annotated directory in the environment variables"
            vars.setProperty("thucydides.annotated.requirements.dir", "otherannotatedstories")
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load requirements"
            def capabilities = capabilityProvider.getRequirements()
        then: "the requirements are the one found in otherannotatedstories"
            capabilities.collect{it.name} == ["Theother1", "Theother2"]
    }
}

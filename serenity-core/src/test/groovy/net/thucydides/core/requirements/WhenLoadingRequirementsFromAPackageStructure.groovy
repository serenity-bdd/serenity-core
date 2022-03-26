package net.thucydides.core.requirements

import junittestcases.samples.fruit.RedAndGreenApples
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.assertj.core.util.Files
import spock.lang.Specification

class WhenLoadingRequirementsFromAPackageStructure extends Specification {
    public static final String ROOT_DIRECTORY = "annotatedstorieswithcontents"

    def setup() {
        if (new File("target/site/serenity/requirements/annotatedstorieswithcontents-package-requirements.json").exists()) {
            Files.delete(new File("target/site/serenity/requirements/annotatedstorieswithcontents-package-requirements.json"))
        }
    }

    def "Should be able to load capabilities from the default package structure"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.SERENITY_TEST_ROOT.propertyName, ROOT_DIRECTORY)
            RequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars)
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements();
            def capabilityNames = capabilities.collect {it.name}
            def capabilitiyTexts = capabilities.collect {it.narrative.renderedText.replace("\r\n","\n").replace("\r","\n")}
        then:
            capabilityNames == ["Apples", "Nice zucchinis", "Potatoes"]
            capabilitiyTexts == ["apples  \nThis is a narrative  \nFor apples  ", "This is a narrative  \nFor Nice Zuchinnis  ",
                    "This is a narrative  \nFor a potato  "]
    }

    def "Should be able to load capabilities from junit test cases in a specified package"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, "junittestcases.samples")
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load the available requirements"
            def capabilities = capabilityProvider.getRequirements();
            def capabilityNames = capabilities.collect {it.name}
        then:
            capabilityNames == ["Fruit", "Veges"]
        and:
            capabilities[0].children.collect {it.name} == ["Oranges","Red and green apples"]
        and:
            capabilities[1].children.collect {it.name} == ["Cucumbers"]
    }

    def "Should be able to load capabilities from junit test cases in a specified package using the default test root"() {
        given: "We are using the Annotation provider"
        def vars = new MockEnvironmentVariables()
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.propertyName, "junittestcases.samples")
        RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load the available requirements"
        def capabilities = capabilityProvider.getRequirements();
        def capabilityNames = capabilities.collect {it.name}
        then:
        capabilityNames == ["Fruit", "Veges"]
        and:
        capabilities[0].children.collect {it.name} == ["Oranges", "Red and green apples"]
        and:
        capabilities[1].children.collect {it.name} == ["Cucumbers"]
    }

    def "Should get correct requirement tags from JUnit test outcomes"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty("thucydides.test.root", "junittestcases.samples")
//            vars.setProperty("thucydides.requirement.types", "feature,story")
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "we run a junit test"
            def testOutcome = TestOutcome.forTest("someTest", RedAndGreenApples.class)
        and: "We look up the tags for this test"
            def tags = capabilityProvider.getTagsFor(testOutcome);
        then:
            tags.size() == 2
            tags.contains(TestTag.withName("Fruit").andType("feature"))
            tags.contains(TestTag.withName("Fruit/Red and green apples").andType("story"))
    }

//    def "Requirements tags should match test outcome tag"() {
//        given: "We are using the Annotation provider"
//            def vars = new MockEnvironmentVariables()
//            vars.setProperty("thucydides.test.root", "junittestcases.samples")
//            vars.setProperty("thucydides.requirement.types", "feature,story")
//            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
//        when: "we run a junit test"
//            def testOutcome = TestOutcome.forTest("someTest", RedAndGreenApples.class)
//        and: "We look up the tags for this test"
//            def requirementsTags = capabilityProvider.getTagsFor(testOutcome);
//        then:
//            requirementsTags.containsAll(testOutcome.getTags());
//    }

    def "Should get correct requirement tags from flat JUnit test outcomes"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty("thucydides.test.root", "junittestcases.samples.fruit")
            vars.setProperty("thucydides.requirement.types", "story")
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "we run a junit test"
            def testOutcome = TestOutcome.forTest("someTest", RedAndGreenApples.class)
        and: "We look up the tags for this test"
            def tags = capabilityProvider.getTagsFor(testOutcome);
        then:
            tags.size() == 1
            tags.contains(TestTag.withName("Red and green apples").andType("story"))
    }

    def "Should consider a JUnit test as a story or feature, no matter what level it is in the requirement structure"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty("thucydides.test.root", "junittestcases.samples.fruit")
            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "we run a junit test"
            def testOutcome = TestOutcome.forTest("someTest", RedAndGreenApples.class)
        and: "We look up the tags for this test"
            def tags = capabilityProvider.getTagsFor(testOutcome);
        then:
            tags.size() == 1
            tags.contains(TestTag.withName("Red and green apples").andType("story"))
    }

    def "Should be able to load stories from junit test cases in a specified package using a flat structure"() {
        given: "We are using the Annotation provider"
            def vars = new MockEnvironmentVariables()
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_TEST_ROOT.propertyName, "junittestcases.samples.fruit")
            vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_REQUIREMENT_TYPES.propertyName, "story")

            RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load the available requirements"
            def stories = capabilityProvider.getRequirements();
            def storyNames = stories.collect {it.name}
            def storyTypes = stories.collect {it.type}
        then:
            storyNames == ["Oranges","Red and green apples"]
        and:
            storyTypes == ["story","story"]
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
            capabilityNames == ["Plant potatoes", "Big potatoes"]
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
            capabilityTypes == ["feature","feature"]
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
            capabilities.get(2).children.collect{it.type} == ["epic","epic"]
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

    def "should read requirements from packages containing only package-info"() {
        given: "We using the package requirement provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            vars.setProperty("serenity.test.root", "packagerequirements")
            def capabilityProvider = new PackageRequirementsTagProvider(vars)
        when: "We load requirements"
            def capabilities = capabilityProvider.getRequirements()
        then: "the requirements are the one found in otherannotatedstories"
            capabilities.collect{it.name} == ["Apples", "Nice zucchinis", "Pears", "Potatoes"]
        and: "the requirements narratives are read from the package-info files"
            Requirement apples = capabilities.find { it.name == 'Apples' }
            apples.narrative.text.contains("For apples")
        and:
            Requirement zucchinis = capabilities.find { it.name == 'Nice zucchinis' }
            zucchinis.narrative.text.contains("For Nice Zuchinnis")

    }

}

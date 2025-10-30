package net.thucydides.core.requirements

import annotatedstorieswithcontents.apples.BuyApples
import annotatedstorieswithcontents.potatoes.big_potatoes.PlantBigPotatoes
import net.serenitybdd.annotations.Narrative
import net.thucydides.model.domain.Story
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestTag
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.requirements.PackageAnnotationBasedTagProvider
import net.thucydides.model.requirements.annotations.NarrativeFinder
import spock.lang.Specification

class WhenReadingTagsFromAnnotations extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should get narrative from a concrete class"() {
        given:
            def testClass = BuyApples
        when:
            Optional<Narrative> narrative = NarrativeFinder.forClass(testClass)
        then:
            narrative.isPresent()
        and:
            narrative.get().title() == "Title for test 1"
    }

    def "should get narrative from a package-info class"() {
        given:
            def testClass = Class.forName("annotatedstorieswithcontents.apples.package-info")
        when:
            Optional<Narrative> narrative = NarrativeFinder.forClass(testClass)
        then:
            narrative.isPresent()
        and:
            narrative.get().title() == "apples"
    }


    def "should read requirements from test annotations defined in THUCYDIDES_TEST_ROOT"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
        when:
            def requirements = tagProvider.getRequirements()
        then:
            requirements.collect { it.name } == ["Apples", "Nice zucchinis", "Potatoes"]
        and:
            requirements[2].children.collect { it.name } == ["Plant potatoes", "Big potatoes"]
        and:
            requirements[2].children[1].children[0].collect { it.name }== ["Plant big potatoes"]
    }

    def "should read requirements from the stored JSON file if the classes are unavailable"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
            tagProvider.getRequirements()
        and:
            def anotherTagProvider = new PackageAnnotationBasedTagProvider(environmentVariables) {

                @Override
                protected List<Class<?>> loadClassesFromPath() {
                    return []
                }
            }
        when:
            def requirements = anotherTagProvider.getRequirements()
        then:
            requirements.collect { it.name } == ["Apples", "Nice zucchinis", "Potatoes"]
        and:
            requirements[2].children.collect { it.name } == ["Plant potatoes", "Big potatoes"]
        and:
            requirements[2].children[1].children[0].collect { it.name }== ["Plant big potatoes"]
    }

    def "should return no requirements if nothing is available"() {
        given:
            def anotherTagProvider = new PackageAnnotationBasedTagProvider(environmentVariables) {

            @Override
            protected List<Class<?>> loadClassesFromPath() {
                return []
            }
        }
        when:
            def requirements = anotherTagProvider.getRequirements()
        then:
            requirements.isEmpty()
    }

    def "should get all tags for a given outcome"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
        and:
            def testOutcome = TestOutcome.forTest("someSampleTest", PlantBigPotatoes)
        when:
            def tags = tagProvider.getTagsFor(testOutcome)
        then:
            tags.collect { it.name }.containsAll(["Big potatoes/Plant big potatoes", "Potatoes/Big potatoes", "Potatoes"])
    }

    def "should get all tags for non-JUnit outcomes"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
        and:
            def testOutcome = TestOutcome.forTestInStory("some test", Story.called("SomeStory").withPath("potatoes/big_potatoes/SomeStory.story"))
        when:
            def tags = tagProvider.getTagsFor(testOutcome)
        then:
            tags.collect { it.name }.containsAll(["Potatoes/Big potatoes", "Potatoes"])
    }

    def "should return absent for an unknown requirement"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
        and:
            def tag = TestTag.withName("Apples").andType("unknown")
        when:
            def requirement = tagProvider.getRequirementFor(tag)
        then:
            !requirement.isPresent()
    }
}

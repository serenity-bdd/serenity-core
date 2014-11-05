package net.thucydides.core.requirements

import annotatedstorieswithcontents.apples.TestSample1
import annotatedstorieswithcontents.potatoes.big_potatoes.BigPotatoeSampleTest1
import com.google.common.base.Optional
import net.thucydides.core.annotations.Narrative
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.annotations.NarrativeFinder
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenReadingTagsFromAnnotations extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should get narrative from a concrete class"() {
        given:
            def testClass = TestSample1
        when:
            Optional<Narrative> narrative = NarrativeFinder.forClass(testClass)
        then:
            narrative.isPresent()
        and:
            narrative.get().title() == "Title for test 1"
    }

    def "should get narrative from a package-info class"() {
        given:
            def testClass = Class.forName("annotatedstorieswithcontents.potatoes.package-info")
        when:
            Optional<Narrative> narrative = NarrativeFinder.forClass(testClass)
        then:
            narrative.isPresent()
        and:
            narrative.get().title() == "Potatoes title"
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
            requirements[2].children.collect { it.name } == ["Test2", "Big potatoes"]
        and:
            requirements[2].children[1].children[0].collect { it.name }== ["Big potatoe sample test1"]
    }

    def "should read requirements from the stored JSON file if the classes are unavailable"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
            tagProvider.getRequirements()
        and:
            def anotherTagProvider = new PackageAnnotationBasedTagProvider(environmentVariables) {

                @Override
                protected List<Class<?>> loadClasses() {
                    return []
                }
            }
        when:
            def requirements = anotherTagProvider.getRequirements()
        then:
            requirements.collect { it.name } == ["Apples", "Nice zucchinis", "Potatoes"]
        and:
            requirements[2].children.collect { it.name } == ["Test2", "Big potatoes"]
        and:
            requirements[2].children[1].children[0].collect { it.name }== ["Big potatoe sample test1"]
    }

    def "should return no requirements if nothing is available"() {
        given:
            def anotherTagProvider = new PackageAnnotationBasedTagProvider(environmentVariables) {

            @Override
            protected List<Class<?>> loadClasses() {
                return []
            }
        }
        when:
            def requirements = anotherTagProvider.getRequirements()
        then:
            requirements.isEmpty()
    }

    def "should find correct requirement for a test outcome based on its package"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
        and:
            def testOutcome = TestOutcome.forTest("someSampleTest", TestSample1)
        when:
            def requirement = tagProvider.getParentRequirementOf(testOutcome)
        then:
            requirement.isPresent()
        and:
            requirement.get().name == "Test sample1" && requirement.get().type == "story"
    }

    def "should get all tags for a given outcome"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
        and:
            def testOutcome = TestOutcome.forTest("someSampleTest", BigPotatoeSampleTest1)
        when:
            def tags = tagProvider.getTagsFor(testOutcome)
        then:
            tags.collect { it.name }.containsAll(["Big potatoe sample test1", "Big potatoes", "Potatoes"])
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
            tags.collect { it.name }.containsAll(["Big potatoes", "Potatoes"])
    }

    def "should find correct requirement for a given test tag"() {
        given:
            environmentVariables.setProperty("thucydides.test.root","annotatedstorieswithcontents")
            def tagProvider = new PackageAnnotationBasedTagProvider(environmentVariables)
        and:
            def tag = TestTag.withName("Apples").andType("capability")
        when:
            def requirement = tagProvider.getRequirementFor(tag)
        then:
            requirement.isPresent()
        and:
            requirement.get().name == "Apples" && requirement.get().type == "capability"
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
package net.thucydides.core.requirements

import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.issues.IssueTracking
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.html.ReportNameProvider
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider
import net.thucydides.model.requirements.PackageAnnotationBasedTagProvider
import net.thucydides.model.requirements.model.Example
import net.thucydides.model.requirements.model.Requirement
import net.thucydides.model.requirements.reports.MultipleSourceRequirmentsOutcomeFactory
import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory
import net.thucydides.model.requirements.reports.RequirementsOutcomes
import spock.lang.Specification

class WhenCreatingARequirement extends Specification {

    def requirementsProviders
    def setup() {
        def vars = new MockEnvironmentVariables()
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, "annotatedstories")
        requirementsProviders = [new FileSystemRequirementsTagProvider(), new PackageAnnotationBasedTagProvider(vars)]
    }

    def issueTracking = Mock(IssueTracking)

    def "Should create a requirement using a simple builder"() {

       when: "we create a simple requirement using a builder"
            def requirement = Requirement.named("some_requirement")
                                         .withOptionalDisplayName("a longer name for display purposes")
                                         .withOptionalCardNumber("CARD-1")
                                         .withType("capability")
                                         .withNarrative("as a someone I want something so that something else")
       then: "we should have a correctly instantiated requirement"
           requirement != null
    }

    def "should report no test results for requirements without associated tests"() {
        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
        RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, new ReportNameProvider())
        when: "we generate the capability outcomes"
        RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the test results for the requirements should be empty"
            def requirementsTestCount = outcomes.requirementOutcomes.collect {it.testOutcomes.total}
            requirementsTestCount == [0,0,0,0,0,0,0]
    }

    def "should be able to optionally record the examples used to define a requirement"() {
        when: "we create a simple requirement using a builder"
            def requirement = Requirement.named("some_requirement")
                    .withOptionalDisplayName("a longer name for display purposes")
                    .withOptionalCardNumber("CARD-1")
                    .withType("capability")
                    .withNarrative("as a someone I want something so that something else")
        and: "we associate it with some examples"
            requirement = requirement.withExample(Example.withDescription("The client buys a blue widget and has it delivered.")
                                                         .andNoCardNumber())
        then: "we should have a correctly instantiated requirement"
            requirement.examples.collect { it.description }  == ["The client buys a blue widget and has it delivered."]
    }

    def "should be able to record several examples"() {
        when: "we create a simple requirement using a builder"
        def requirement = Requirement.named("some_requirement")
                .withOptionalDisplayName("a longer name for display purposes")
                .withOptionalCardNumber("CARD-1")
                .withType("capability")
                .withNarrative("as a someone I want something so that something else")
        and: "we associate it with some examples"
            requirement = requirement.withExample(Example.withDescription("The client buys a blue widget and has it delivered.").andCardNumber("CARD-1"))
                                     .withExample(Example.withDescription("The client buys a red widget and has it delivered.").andCardNumber("CARD-2"))
        then: "we should have a correctly instantiated requirement"
            requirement.examples.collect { it.description } == ["The client buys a blue widget and has it delivered.",
                                                                "The client buys a red widget and has it delivered."]

        requirement.examples.collect { it.cardNumber.get() } == ["CARD-1","CARD-2"]

    }

    def "examples should have a descriptive string version"() {
        given:
            def example = Example.withDescription("The client buys a blue widget.").andCardNumber("CARD-1")
        when:
            def stringVersion = example.toString()
        then:
            stringVersion ==  "The client buys a blue widget. [CARD-1]"
    }

    def "should be able to record several examples at the same time"() {
        when: "we create a simple requirement including some examples"
        def requirement = Requirement.named("some_requirement")
                .withOptionalDisplayName("a longer name for display purposes")
                .withOptionalCardNumber("CARD-1")
                .withType("capability")
                .withNarrative("as a someone I want something so that something else")
                .withExamples([Example.withDescription("The client buys a blue widget and has it delivered.").andCardNumber("CARD-1"),
                               Example.withDescription("The client buys a red widget and has it delivered.").andCardNumber("CARD-2")])
        then: "we should have a correctly instantiated requirement"
            requirement.hasExamples()
            requirement.exampleCount == 2
            requirement.examples.collect { it.description } == ["The client buys a blue widget and has it delivered.",
                                                                "The client buys a red widget and has it delivered."]
    }

}

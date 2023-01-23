package net.thucydides.core.model

import spock.lang.Specification

class WhenRepresentingManualTestOutcomes extends Specification {

    def "a test outcome should be considered automated by default"() {
        when:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
        then:
            !outcome.isManual()
    }

    def "a manual test outcome can be defined"() {
        when:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story")).setToManual();
        then:
            outcome.isManual()
    }

    def "a manual test does not need to have a start time"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story")).setToManual();
        when:
            outcome.clearStartTime()
        then:
            outcome.startTimeNotDefined
    }

    def "a manual test can have a description defined in an external source"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story")).setToManual();
        when:
            outcome.setDescription("Some description")
        then:
            outcome.descriptionText.isPresent()
        and:
            outcome.descriptionText.get() == "Some description"
    }

    def "a test can have a description that follows the first line of the story title"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story")).setToManual();
        when:
            outcome.setTitle("someTest\nSome description")
        then:
            outcome.descriptionText.isPresent()
        and:
            outcome.descriptionText.get() == "Some description"
    }

    def "a new test outcome can be created with extra issue keys"() {
        when:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"))
                                     .setToManual()
                                     .withIssues(["MYPROJ-123"]);
        then:
            outcome.issues == ["MYPROJ-123"]
    }

    def "additional issues come after the main issues"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"))
                    .setToManual()
                    .withIssues(["MYPROJ-123"]);
        when:
            outcome.addIssues(["MYPROJ-012"])
        then:
            outcome.issues == ["MYPROJ-123","MYPROJ-012"]
    }

}

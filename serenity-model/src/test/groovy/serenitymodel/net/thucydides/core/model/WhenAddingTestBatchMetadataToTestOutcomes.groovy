package serenitymodel.net.thucydides.core.model

import serenitymodel.net.thucydides.core.model.Story
import serenitymodel.net.thucydides.core.model.TestOutcome
import spock.lang.Specification

class WhenAddingTestBatchMetadataToTestOutcomes extends Specification {

    def "should be able to specify the project for a test outcome"() {
        given:
            def outcome = TestOutcome.forTestInStory("someTest", Story.withId("1","story"));
        when:
            outcome.forProject("Some Project")
        then:
            outcome.project == "Some Project"
    }
}

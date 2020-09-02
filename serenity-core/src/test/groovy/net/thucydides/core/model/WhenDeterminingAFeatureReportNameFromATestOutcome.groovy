package net.thucydides.core.model

import net.thucydides.core.digest.Digest
import spock.lang.Specification

class WhenDeterminingAFeatureReportNameFromATestOutcome extends Specification {

    def "Should use parent directory with feature files if present"() {
        given:
            Story story = new Story("test-reports",
                    "Test Reports",
                    null,
                    "reports_examples/test_reports.feature").asFeature()
        when:
            def reportName = story.getReportName()
        then:
            reportName == Digest.ofTextValue("feature_reports_examples_sl_test_reports") + ".html"
    }

    def "Should use parent directory with story files if present"() {
        given:
        Story story = new Story("test-reports",
                "Test Reports",
                null,
                "reports_examples/test_reports.story")
        when:
            def reportName = story.getReportName()
        then:
            reportName == Digest.ofTextValue("story_reports_examples_sl_test_reports") + ".html"
    }


    def "Should use parent directory with feature files if present for deep directory structures"() {
        given:
        Story story = new Story("test-reports",
                "Test Reports",
                null,
                "reports/reports_examples/test_reports.feature").asFeature()
        when:
        def reportName = story.getReportName()
        then:
        reportName == Digest.ofTextValue("feature_reports_examples_sl_test_reports") + ".html"
    }

    def "Should use parent directory with junit test results for class-based directory structures"() {
        given:
        Story story = new Story("net.serenitybdd.demos.todos.screenplay.features.accessing_the_application.LearnAboutTheApplication",
                "Learn about the application",
                null,
                "accessing_the_application").asFeature()
        when:
        def reportName = story.getReportName()

        then:
        reportName == Digest.ofTextValue("feature_accessing_the_application_sl_learn_about_the_application") + ".html"
    }
}

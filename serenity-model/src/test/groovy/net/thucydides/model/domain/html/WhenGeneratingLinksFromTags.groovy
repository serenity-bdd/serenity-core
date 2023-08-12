package net.thucydides.model.domain.html

import net.thucydides.model.domain.ReportNamer
import net.thucydides.model.domain.ReportType
import net.thucydides.model.domain.TestTag
import net.thucydides.model.reports.html.ReportNameProvider
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenGeneratingLinksFromTags extends Specification {

    def environmentVariables = new MockEnvironmentVariables();
    def reportNamer = new ReportNamer(ReportType.HTML, false)
    def reportNameProvider = new ReportNameProvider(ReportNameProvider.NO_CONTEXT, reportNamer)

    def "should report requirement reports for requirement tags"() {
        given:
            TestTag featureTag = TestTag.withName("Grow Apples").andType("feature");
        when:
            def reportName = reportNameProvider.forRequirementOrTag(featureTag)
        then:
            reportName == "ec1d09f6cf4c5b516620310b7fdd4850_feature_grow_apples.html"
    }

    def "should report tag reports for normal tags"() {
        given:
        TestTag featureTag = TestTag.withName("red").andType("color");
        when:
        def reportName = reportNameProvider.forRequirementOrTag(featureTag)
        then:
        reportName == "28a1e66ee7b035c52fb1585590fa942a_color_red.html"
    }

    def "should report tag reports for simple tags"() {
        given:
        TestTag featureTag = TestTag.withValue("blue");
        when:
        def reportName = reportNameProvider.forRequirementOrTag(featureTag)
        then:
        reportName == "188e2395e3d3f65750c9288b5cab5b2d_tag_blue.html"
    }

    def "should report issue link for JIRA issue tags"() {
        given:
        TestTag featureTag = TestTag.withValue("issue:PROJ-123");
        environmentVariables.setProperty("jira.url","https://my.jira.server")
        reportNameProvider.environmentVariables = environmentVariables;
        when:
        def reportName = reportNameProvider.forRequirementOrTag(featureTag)
        then:
        reportName == "https://my.jira.server/browse/PROJ-123"
    }

    def "should report issue link for generic issue URLs"() {
        given:
        TestTag featureTag = TestTag.withValue("issue:PROJ-123");
        environmentVariables.setProperty("serenity.issue.tracker.url","https://my.issue.tracker/issues/{0}")
        reportNameProvider.environmentVariables = environmentVariables;
        when:
        def reportName = reportNameProvider.forRequirementOrTag(featureTag)
        then:
        reportName == "https://my.issue.tracker/issues/PROJ-123"
    }

}

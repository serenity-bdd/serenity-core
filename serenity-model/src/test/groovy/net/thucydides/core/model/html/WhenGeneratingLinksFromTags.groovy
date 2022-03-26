package net.thucydides.core.model.html

import net.thucydides.core.model.ReportNamer
import net.thucydides.core.model.ReportType
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.util.MockEnvironmentVariables
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
            reportName == "feature_grow_apples.html"
    }

    def "should report tag reports for normal tags"() {
        given:
        TestTag featureTag = TestTag.withName("red").andType("color");
        when:
        def reportName = reportNameProvider.forRequirementOrTag(featureTag)
        then:
        reportName == "color_red.html"
    }

    def "should report tag reports for simple tags"() {
        given:
        TestTag featureTag = TestTag.withValue("blue");
        when:
        def reportName = reportNameProvider.forRequirementOrTag(featureTag)
        then:
        reportName == "tag_blue.html"
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

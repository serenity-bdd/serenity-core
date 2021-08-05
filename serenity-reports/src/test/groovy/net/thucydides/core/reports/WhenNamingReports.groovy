package net.thucydides.core.reports

import net.thucydides.core.digest.Digest
import net.thucydides.core.reports.html.ReportNameProvider
import net.thucydides.core.requirements.model.Requirement
import spock.lang.Specification

class WhenNamingReports extends Specification {


    def "should find a unique name for a report"() {
        given:
            def reportNameProvider = new ReportNameProvider()
        when:
            def reportName = reportNameProvider.forTestResult("success")
        then:
            reportName == Digest.ofTextValue("result_success") + ".html"
    }

    def "should find a unique name for a report within a context"() {
        given:
            def reportNameProvider = new ReportNameProvider("sometest")
        when:
            def reportName = reportNameProvider.forTestResult("success")
        then:
            reportName == Digest.ofTextValue("context_sometest_result_success") + ".html"
    }

    def "should find a unique prefixed name for a report"() {
        given:
            def reportNameProvider = new ReportNameProvider()
        when:
            def reportName = reportNameProvider.withPrefix("pre").forTestResult("success")
        then:
            reportName == Digest.ofTextValue("context_pre_result_success") + ".html"
    }

    def "should find a unique name for a tag report within a context"() {
        given:
        def reportNameProvider = new ReportNameProvider("sometest")
        when:
        def reportName = reportNameProvider.forTag("tagvalue")
        then:
        reportName == Digest.ofTextValue("context_sometest_tagvalue") + ".html"
    }

    def "should find a unique prefixed name for a tag report"() {
        given:
        def reportNameProvider = new ReportNameProvider()
        when:
        def reportName = reportNameProvider.withPrefix("pre").forTag("tagvalue")
        then:
        reportName == Digest.ofTextValue("context_pre_tagvalue") + ".html"
    }

    def "should find a unique name for a tag type report"() {
        given:
        def reportNameProvider = new ReportNameProvider()
        when:
        def reportName = reportNameProvider.forTagType("tagvalue")
        then:
        reportName == Digest.ofTextValue("tagtype_tagvalue") + ".html"
    }

    def "should find a unique name for a tag type report within a context"() {
        given:
        def reportNameProvider = new ReportNameProvider("sometest")
        when:
        def reportName = reportNameProvider.forTagType("tagvalue")
        then:
        reportName == Digest.ofTextValue("context_sometest_tagtype_tagvalue") + ".html"
    }

    def "should find a unique prefixed name for a tag type report"() {
        given:
        def reportNameProvider = new ReportNameProvider()
        when:
        def reportName = reportNameProvider.withPrefix("pre").forTagType("tagvalue")
        then:
        reportName == Digest.ofTextValue("context_pre_tagtype_tagvalue") + ".html"
    }

    def "should find a unique  name for a CSV report"() {
        given:
            def reportNameProvider = new ReportNameProvider()
        when:
            def reportName = reportNameProvider.forCSVFiles().forTestResult("success")
        then:
            reportName == Digest.ofTextValue("result_success") + ".csv"
    }

    def "should find a unique prefixed name for a CSV report"() {
        given:
        def reportNameProvider = new ReportNameProvider("sometest")
        when:
        def reportName = reportNameProvider.forCSVFiles().forTestResult("success")
        then:
        reportName == Digest.ofTextValue("context_sometest_result_success") + ".csv"
    }

    def "should find a unique  name for a requirement report"() {
        given:
            def reportNameProvider = new ReportNameProvider()
            def requirement = Mock(Requirement)
            requirement.qualifiedName() >> "foo"
            requirement.getType() >> "feature"
        when:
            def reportName = reportNameProvider.forRequirement(requirement)
        then:
            reportName == Digest.ofTextValue("feature_foo") + ".html"
    }


    def "should find a unique  name for a requirement report in context"() {
        given:
            def reportNameProvider = new ReportNameProvider("sometest")
            def requirement = Mock(Requirement)
            requirement.qualifiedName() >> "foo"
            requirement.getType() >> "feature"
        when:
            def reportName = reportNameProvider.forRequirement(requirement)
        then:
            reportName == Digest.ofTextValue("context_sometest_feature_foo") + ".html"
    }



}

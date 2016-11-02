package net.thucydides.core.reports

import net.thucydides.core.model.TestTag
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenManagingTestResultTags extends Specification {

    def testOutcomes = new TestOutcomesBuilder().defaultResults

    def "should list all of the tag names in a set of outcomes"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagNames == ['chocolate','orange', 'widget feature', 'widget feature/purchase new widget']
    }

    def "should list all of the tag types"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagTypes == ['color','feature','flavor','story']
    }


    def "should list all of the tag types configured to appear on the menu"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.report.tag.menus","color,flavor")
        when:
            ReportOptions options = new ReportOptions(environmentVariables)
        then:
            options.firstClassTagTypes == ['color','flavor']
    }

    def "by default no tag types are configured to appear on the menu"() {
        given:
        def environmentVariables = new MockEnvironmentVariables()
        when:
        ReportOptions options = new ReportOptions(environmentVariables)
        then:
        options.firstClassTagTypes.isEmpty()
    }

}
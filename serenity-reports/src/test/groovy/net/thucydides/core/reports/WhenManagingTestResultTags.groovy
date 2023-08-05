package net.thucydides.core.reports

import net.thucydides.model.domain.TestTag
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.reports.ReportOptions
import spock.lang.Specification

class WhenManagingTestResultTags extends Specification {

    def testOutcomes = new TestOutcomesBuilder().defaultResults

    def "test outcomes tags should be returned as a set of unique tags"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withValue("flavor:chocolate")])
            testOutcomes.tests[0].addTags([TestTag.withValue("color:orange")])
        and:
            testOutcomes.tests[1].addTags([TestTag.withValue("flavor:chocolate")])
            testOutcomes.tests[1].addTags([TestTag.withValue("color:red")])

        then:
            testOutcomes.tags as Set == [TestTag.withValue("flavor:chocolate"),
                                      TestTag.withValue("color:orange"),
                                      TestTag.withValue("color:red")] as Set
    }


    def "should list all of the tag names in a set of outcomes"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagNames == ['chocolate',
                                      'orange',
                                      ]
    }

    def "should list all of the tag types"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagTypes == ['color','flavor']
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

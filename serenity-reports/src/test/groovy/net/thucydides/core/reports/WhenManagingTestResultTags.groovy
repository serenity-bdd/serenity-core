package net.thucydides.core.reports

import net.thucydides.core.model.TestTag
import net.thucydides.core.environment.MockEnvironmentVariables
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
            testOutcomes.tags.size() == 5
            testOutcomes.tags as Set == [TestTag.withValue("feature:widget feature"),
                                      TestTag.withValue("flavor:chocolate"),
                                      TestTag.withValue("feature:net/thucydides/core/reports/TestOutcomesBuilder/WidgetFeature/PurchaseNewWidget"),
                                      TestTag.withValue("color:orange"),
                                      TestTag.withValue("color:red")] as Set
    }


    def "should list all of the tag names in a set of outcomes"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagNames == ['chocolate',
                                      'net/thucydides/core/reports/testoutcomesbuilder/widgetfeature/purchasenewwidget',
                                      'orange',
                                      'widget feature'
                                      ]
    }

    def "should list all of the tag types"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagTypes == ['color','feature','flavor']
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

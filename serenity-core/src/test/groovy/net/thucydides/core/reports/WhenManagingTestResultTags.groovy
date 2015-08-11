package net.thucydides.core.reports

import net.thucydides.core.model.TestTag
import spock.lang.Specification

class WhenManagingTestResultTags extends Specification {

    def testOutcomes = new TestOutcomesBuilder().defaultResults

    def "should list all of the tag names in a set of outcomes"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagNames == ['chocolate','orange','purchase new widget', 'widget feature']
    }

    def "should list all of the tag types"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagTypes == ['color','feature','flavor','story']
    }


    def "should list all of the tag types that are not requirements"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.firstClassTagTypes == ['color','flavor']
    }

    def "should list all of the tag types that are not requirements or versions"() {
        when:
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
            testOutcomes.tests[0].addTags([TestTag.withName("1.1").andType("version")])
        then:
            testOutcomes.firstClassTagTypes == ['color','flavor']
    }

}
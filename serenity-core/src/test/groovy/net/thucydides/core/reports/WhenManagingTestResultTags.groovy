package net.thucydides.core.reports

import net.serenitybdd.core.injectors.EnvironmentDependencyInjector
import net.thucydides.core.guice.Injectors
import net.thucydides.core.model.TestTag
import net.thucydides.core.statistics.service.FeatureStoryTagProvider
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.util.SystemEnvironmentVariables
import net.thucydides.core.webdriver.Configuration
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import spock.lang.Specification

class WhenManagingTestResultTags extends Specification {

    def SystemEnvironmentVariables environmentVariables = Injectors.getInjector().getProvider(EnvironmentVariables.class).get();

    def cleanup() {
        environmentVariables.clearProperty(FeatureStoryTagProvider.getAddStoryTagsPropertyName())
    }

    def testOutcomes = new TestOutcomesBuilder().defaultResults

    def "should list all of the tag names in a set of outcomes with story tag from test case"() {

        when:
            environmentVariables.setProperty(FeatureStoryTagProvider.getAddStoryTagsPropertyName(), "true")
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagNames == ['chocolate','orange','purchase new widget', 'widget feature']
    }

    def "should list all of the tag names in a set of outcomes"() {

        when:
        environmentVariables.setProperty(FeatureStoryTagProvider.getAddStoryTagsPropertyName(), "false")
        testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
        testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
        testOutcomes.tagNames == ['chocolate','orange','widget feature']
    }

    def "should list all of the tag types with story tag from test case"() {
        when:
            environmentVariables.setProperty(FeatureStoryTagProvider.getAddStoryTagsPropertyName(), "true")
            testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
            testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
            testOutcomes.tagTypes == ['color','feature','flavor','story']
    }

    def "should list all of the tag types"() {
        when:
        environmentVariables.setProperty(FeatureStoryTagProvider.getAddStoryTagsPropertyName(), "false")
        testOutcomes.tests[0].addTags([TestTag.withName("chocolate").andType("flavor")])
        testOutcomes.tests[0].addTags([TestTag.withName("orange").andType("color")])
        then:
        testOutcomes.tagTypes == ['color','feature','flavor']
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
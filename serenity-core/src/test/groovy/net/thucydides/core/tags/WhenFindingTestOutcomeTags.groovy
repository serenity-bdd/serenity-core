package net.thucydides.core.tags

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestTag
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.statistics.service.FeatureStoryTagProvider
import spock.lang.Specification

/**
 * A description goes here.
 * User: john
 * Date: 26/03/2014
 * Time: 8:55 AM
 */
class WhenFindingTestOutcomeTags extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    class SomeTest {
        def aTest() {}
    }

    def "should get story tag from the test class by default"() {
        given:
            def tagProvider = new FeatureStoryTagProvider(environmentVariables)
            def testOutcome = TestOutcome.forTest("aTest", SomeTest)
        when:
            def tags = tagProvider.getTagsFor(testOutcome)
        then:
            tags.contains(TestTag.withName("net/thucydides/core/tags/WhenFindingTestOutcomeTags/SomeTest").andType("feature"))
    }

    def "should not get story tag from the test class if "() {
        given:
            environmentVariables.setProperty("use.test.case.for.story.tag","false")
        and:
            def tagProvider = new FeatureStoryTagProvider(environmentVariables)
            def testOutcome = TestOutcome.forTest("aTest", SomeTest)
        when:
            def tags = tagProvider.getTagsFor(testOutcome)
        then:
            tags.isEmpty()
    }

}

package net.serenitybdd.core.history

import net.thucydides.core.model.Story
import net.thucydides.core.model.flags.FlagProvider
import net.thucydides.core.reports.TestOutcomes
import spock.lang.Specification

import static net.thucydides.core.model.TestOutcome.forTestInStory

class WhenDisplayingTestOutcomeFailureFlags extends Specification {

    def "test outcomes should have no flags by default"() {
        when:
        def testOutcome = forTestInStory("my test", Story.called("my story"))
        then:
        testOutcome.getFlags().isEmpty()
    }

    def "test outcomes should provide the total number of flags"() {
        given:
            def newFailureFlagProvider = Mock(FlagProvider)
            newFailureFlagProvider.getFlagsFor(_) >> [ NewFailure.FLAG ]
        when:
            def testOutcome1 = forTestInStory("my test 1", Story.called("my story")).withFlagProvider(newFailureFlagProvider)
            def testOutcome2 = forTestInStory("my test 2", Story.called("my story")).withFlagProvider(newFailureFlagProvider)
            def testOutcome3 = forTestInStory("my test 4", Story.called("my story"))

            def testOutcomes = TestOutcomes.of([testOutcome1, testOutcome2, testOutcome3])
        then:
            testOutcomes.haveFlags() == true
            testOutcomes.flags.size() == 1
            testOutcomes.getFlagCounts().get(NewFailure.FLAG) == 2


    }
}
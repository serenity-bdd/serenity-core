package serenitycore.net.serenitybdd.core.history

import serenitymodel.net.serenitybdd.core.history.HistoricalFlagProvider
import serenitymodel.net.serenitybdd.core.history.NewFailure
import serenitymodel.net.serenitybdd.core.history.PreviousTestOutcome
import serenitymodel.net.serenitybdd.core.history.TestOutcomeSummaryRecorder
import serenitymodel.net.thucydides.core.model.Story
import serenitymodel.net.thucydides.core.model.TestOutcome
import serenitymodel.net.thucydides.core.model.TestResult
import serenitymodel.net.thucydides.core.steps.TestFailureCause
import serenitymodel.net.thucydides.core.util.EnvironmentVariables
import serenitymodel.net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.WebDriverException
import spock.lang.Specification

class WhenCheckingForNewTestFailures extends Specification {

    def "test outcomes are reported for new failures"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

        TestOutcomeSummaryRecorder recorder = Mock(TestOutcomeSummaryRecorder)

        PreviousTestOutcome previousTestOutcome1 = new PreviousTestOutcome("some story:some test","a test",TestResult.SUCCESS,"")
            PreviousTestOutcome previousTestOutcome2 = new PreviousTestOutcome("some story:another test","a test",TestResult.SUCCESS,"")
            recorder.loadSummaries() >> [previousTestOutcome1, previousTestOutcome2]
        and:
            environmentVariables.setProperty("show.history.flags","true")
        HistoricalFlagProvider flagProvider = new HistoricalFlagProvider(environmentVariables,recorder)
        when:
            TestOutcome testOutcome1 = TestOutcome.forTestInStory("some test", Story.called("some story"))
            TestOutcome testOutcome2 = TestOutcome.forTestInStory("another test", Story.called("some story"))
            TestOutcome testOutcome3 = TestOutcome.forTestInStory("a new test", Story.called("some story"))

            testOutcome1.appendTestFailure(TestFailureCause.from(new AssertionError("Oh crap!")))
        then:
            flagProvider.getFlagsFor(testOutcome1) == [NewFailure.FLAG] as Set
            flagProvider.getFlagsFor(testOutcome2) == [] as Set
            flagProvider.getFlagsFor(testOutcome3) == [] as Set
    }

    def "the same failure should not be flagged"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

            TestOutcomeSummaryRecorder recorder = Mock(TestOutcomeSummaryRecorder)

            PreviousTestOutcome previousTestOutcome1 = new PreviousTestOutcome("some story:some test","a test",TestResult.FAILURE,"FAILURE;java.lang.AssertionError;Oh crap!;")
            recorder.loadSummaries() >> [previousTestOutcome1]
        and:
            environmentVariables.setProperty("show.history.flags","true")
            HistoricalFlagProvider flagProvider = new HistoricalFlagProvider(environmentVariables,recorder)
        when:
            TestOutcome testOutcome1 = TestOutcome.forTestInStory("some test", Story.called("some story"))
            testOutcome1.appendTestFailure(TestFailureCause.from(new AssertionError("Oh crap!")))
        then:
            flagProvider.getFlagsFor(testOutcome1) == [] as Set
    }

    def "a different failure should be flagged"() {
        given:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

        TestOutcomeSummaryRecorder recorder = Mock(TestOutcomeSummaryRecorder)

        PreviousTestOutcome previousTestOutcome1 = new PreviousTestOutcome("some story:some test","a test",TestResult.FAILURE,"FAILURE;java.lang.AssertionError;Oh crap!;")
        recorder.loadSummaries() >> [previousTestOutcome1]
        and:
        environmentVariables.setProperty("show.history.flags","true")
        HistoricalFlagProvider flagProvider = new HistoricalFlagProvider(environmentVariables,recorder)
        when:
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("some test", Story.called("some story"))
        testOutcome1.appendTestFailure(TestFailureCause.from(new AssertionError("Oh nose!")))
        then:
        flagProvider.getFlagsFor(testOutcome1) == [NewFailure.FLAG] as Set
    }


    def "a different failure type should be flagged"() {
        given:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

        TestOutcomeSummaryRecorder recorder = Mock(TestOutcomeSummaryRecorder)

        PreviousTestOutcome previousTestOutcome1 = new PreviousTestOutcome("some story:some test","a test",TestResult.FAILURE,"FAILURE;java.lang.AssertionError;Oh crap!;")
        recorder.loadSummaries() >> [previousTestOutcome1]
        and:
        environmentVariables.setProperty("show.history.flags","true")
        HistoricalFlagProvider flagProvider = new HistoricalFlagProvider(environmentVariables,recorder)
        when:
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("some test", Story.called("some story"))
        testOutcome1.appendTestFailure(TestFailureCause.from(new WebDriverException("Oh crap!")))
        then:
        flagProvider.getFlagsFor(testOutcome1) == [NewFailure.FLAG] as Set
    }
}
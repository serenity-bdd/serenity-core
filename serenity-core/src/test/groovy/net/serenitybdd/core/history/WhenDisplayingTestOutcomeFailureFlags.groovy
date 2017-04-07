package net.serenitybdd.core.history

import net.thucydides.core.model.Story
import net.thucydides.core.model.flags.FlagProvider
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

import static net.thucydides.core.model.TestOutcome.forTestInStory

class WhenDisplayingTestOutcomeFailureFlags extends Specification {

    def "test outcomes should have no flags by default"() {
        when:
        def testOutcome = forTestInStory("my test", Story.called("my story"))
        then:
        testOutcome.getFlags().isEmpty()
    }

    private static final String UNDEFINED = ""

    @Unroll
    def "You can activate historical flags in test outcomes using the show.historical.flags property and by defining a history directory"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
            String historyDirectoryPath = (historyDirectory == UNDEFINED) ? "" : new File("src/test/resources/" + historyDirectory).getAbsolutePath();
            String sourceDirectoryPath = new File("src/test/resources/json-reports").getAbsolutePath();
        and:
            environmentVariables.setProperty("show.history.flags", historicalFlags)
            environmentVariables.setProperty("serenity.history.directory", historyDirectoryPath)
            environmentVariables.setProperty("serenity.sourceDirectory", sourceDirectoryPath)

            def flagProvider = new HistoricalFlagProvider(environmentVariables)

            def testOutcome = forTestInStory("my test", Story.called("my story"))
        when:
            testOutcome = testOutcome.withFlagProvider(flagProvider)
        then:
            !testOutcome.flags.isEmpty() == testFlagsShouldBeFound
        where:
            historicalFlags | historyDirectory     | testFlagsShouldBeFound
                    "false" | "historical-reports" | false
                    "true"  | "historical-reports" | true
                    "true"  | UNDEFINED            | false
    }

    def "test outcomes should provide the total number of flags"() {
        given:
            def newFailureFlagProvider = Mock(FlagProvider)
            newFailureFlagProvider.getFlagsFor(_) >> [ NewFailure.FLAG ]
            def slowFlagProvider = Mock(FlagProvider)
            slowFlagProvider.getFlagsFor(_) >> [ SlowTest.FLAG ]
        when:
            def testOutcome1 = forTestInStory("my test 1", Story.called("my story")).withFlagProvider(newFailureFlagProvider)
            def testOutcome2 = forTestInStory("my test 2", Story.called("my story")).withFlagProvider(newFailureFlagProvider)
            def testOutcome3 = forTestInStory("my test 3", Story.called("my story")).withFlagProvider(slowFlagProvider)
            def testOutcome4 = forTestInStory("my test 4", Story.called("my story"))

            def testOutcomes = TestOutcomes.of([testOutcome1, testOutcome2, testOutcome3, testOutcome4])
        then:
            testOutcomes.haveFlags() == true
            testOutcomes.flags.size() == 2
            testOutcomes.getFlagCounts().get(NewFailure.FLAG) == 2
            testOutcomes.getFlagCounts().get(SlowTest.FLAG) == 1


    }
}
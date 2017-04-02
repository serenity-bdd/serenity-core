package net.serenitybdd.core.failurehistory

import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenDisplayingTestOutcomeFailureFlags extends Specification {

    def "test outcomes should have no flags by default"() {
        when:
        def testOutcome = TestOutcome.forTestInStory("my test", Story.called("my story"))
        then:
        testOutcome.getFlags().isEmpty()
    }

    private static final String UNDEFINED = ""

    def "You can activate historical flags in test outcomes using the show.historical.flags property and by defining a history directory"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
            String historyDirectoryPath = new File("src/test/resources/" + historyDirectory).getAbsolutePath();
            String sourceDirectoryPath = new File("src/test/resources/json-reports").getAbsolutePath();
        and:
            environmentVariables.setProperty("show.history.flags", historicalFlags)
            environmentVariables.setProperty("serenity.history.directory", historyDirectory)
            environmentVariables.setProperty("serenity.sourceDirectory", sourceDirectoryPath)

            def flagProvider = new HistoricalFlagProvider(environmentVariables)

            def testOutcome = TestOutcome.forTestInStory("my test", Story.called("my story"))
        when:
            testOutcome = testOutcome.withFlagProvider(flagProvider)
        then:
            !testOutcome.getFlags().isEmpty() == testFlagsShouldBeFound
        where:
            historicalFlags | historyDirectory     | testFlagsShouldBeFound
                    "false" | "historical-reports" | false
                    "true"  | "historical-reports" | true
                    "true"  | UNDEFINED            | false

    }

}
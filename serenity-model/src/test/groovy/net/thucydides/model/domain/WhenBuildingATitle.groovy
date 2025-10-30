package net.thucydides.model.domain


import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenBuildingATitle extends Specification {

    MockEnvironmentVariables environmentVariables
    TestOutcome testOutcome
    TitleBuilder titleBuilder

    void setup() {
        environmentVariables = new MockEnvironmentVariables()

        testOutcome = new TestOutcome("Dummy")
        testOutcome.setTitle("Test Title")
        testOutcome.setEnvironmentVariables(environmentVariables)

        titleBuilder = new TitleBuilder(testOutcome, null, environmentVariables, false)
    }

    def "it should return only the outcome title if the showContext attribute is set to false"() {
        given:
        environmentVariables.setProperty("context", "firefox")

        when:
        String title = titleBuilder.getTitle()

        then:
        title == testOutcome.getTitle()
    }

    def "it should return only the outcome title if there is no context"() {
        given:
        titleBuilder = titleBuilder.withContext()

        when:
        String title = titleBuilder.getTitle()

        then:
        title == testOutcome.getTitle()
    }

    def "it should return only the outcome title if the display context environment property is set to false"() {
        given:
        titleBuilder = titleBuilder.withContext()
        environmentVariables.setProperty("context", "blackberry")
        environmentVariables.setProperty("serenity.display.context", "false")

        when:
        String title = titleBuilder.getTitle()

        then:
        title == testOutcome.getTitle()
    }
}

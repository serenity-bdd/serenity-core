package net.thucydides.core.webdriver

import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll


class WhenPeriodicallyRestartingTheBrowser extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    @Unroll
    def "should not restart if not configured"() {
        expect:
            new PeriodicRestart(environmentVariables).forTestNumber(number) == shouldRestart
        where:
             number | shouldRestart
            0      | false
            1      | false
    }

    @Unroll
    def "should restart as configured"() {
        expect:
            environmentVariables.setProperty("serenity.restart.browser.frequency","3")
            new PeriodicRestart(environmentVariables).forTestNumber(number) == shouldRestart
        where:
        number | shouldRestart
            0      | false
            1      | false
            3      | true
            6      | true
            7      | false
    }

}
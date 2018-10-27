package net.thucydides.core.model

import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

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

    @Unroll
    def "it should return a context icon with outcome title for known contexts (#context -> #icon)"() {
        given:
        titleBuilder = titleBuilder.withContext()
        environmentVariables.setProperty("context", context)

        when:
        String title = titleBuilder.getTitle()

        then:
        title == icon + testOutcome.getTitle()

        where:
        context     | icon
        "chrome"    | "<span class='context-icon'><i class='fa fa-chrome' aria-hidden='true'></i></span>"
        "firefox"   | "<span class='context-icon'><i class='fa fa-firefox' aria-hidden='true'></i></span>"
        "safari"    | "<span class='context-icon'><i class='fa fa-safari' aria-hidden='true'></i></span>"
        "opera"     | "<span class='context-icon'><i class='fa fa-opera' aria-hidden='true'></i></span>"
        "ie"        | "<span class='context-icon'><i class='fa fa-internet-explorer' aria-hidden='true'></i></span>"
        "edge"      | "<span class='context-icon'><i class='fa fa-edge' aria-hidden='true'></i></span>"
        "phantomjs" | "<span class='context-icon'><i class='fa fa-snapchat-ghost' aria-hidden='true'></i></span>"
        "linux"     | "<span class='context-icon'><i class='fa fa-linux' aria-hidden='true'></i></span>"
        "mac"       | "<span class='context-icon'><i class='fa fa-apple' aria-hidden='true'></i></span>"
        "windows"   | "<span class='context-icon'><i class='fa fa-windows' aria-hidden='true'></i></span>"
        "android"   | "<span class='context-icon'><i class='fa fa-android' aria-hidden='true'></i></span>"
        "iphone"    | "<span class='context-icon'><i class='fa fa-apple' aria-hidden='true'></i></span>"
    }

    def "it should return the uppercase context name with outcome title for unknown contexts"() {
        given:
        titleBuilder = titleBuilder.withContext()
        environmentVariables.setProperty("context", "blackberry")

        when:
        String title = titleBuilder.getTitle()

        then:
        title.contains("BLACKBERRY")
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

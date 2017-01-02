package net.thucydides.core.model.screenshots

import net.thucydides.core.model.TakeScreenshots
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenConfiguringScreenshotsByClassType extends Specification {


    def environmentVariables = new MockEnvironmentVariables()

    static interface Interaction extends Action {}
    static interface Action {}
    static class SomeAction implements Interaction {}
    static class SomeOtherClass {}

    static class Task {}
    static class SomeTask extends Task {}

    def "should not configure steps by type if not asked to"() {
        when:
            ScreenshotPreferencesByClass screenshotPreferencesByClass = ScreenshotPreferencesByClass.forClass(SomeAction)
                                                                                                    .withEnvironmentVariables(environmentVariables)
        then:
            !screenshotPreferencesByClass.screenshotPreference.isPresent()
    }

    def "should override the screenshot configuration based on the name of a class"() {
        given:
            environmentVariables.setProperty("serenity.take.screenshots.for.action","disabled")
        when:
            ScreenshotPreferencesByClass screenshotPreferencesByClass = ScreenshotPreferencesByClass.forClass(SomeAction)
                                                                                                    .withEnvironmentVariables(environmentVariables)
        then:
            screenshotPreferencesByClass.screenshotPreference.isPresent() && screenshotPreferencesByClass.screenshotPreference.get() == TakeScreenshots.DISABLED
    }

    def "should override the screenshot configuration based on the name of a parent class"() {
        given:
        environmentVariables.setProperty("serenity.take.screenshots.for.tasks","disabled")
        when:
        ScreenshotPreferencesByClass screenshotPreferencesByClass = ScreenshotPreferencesByClass.forClass(SomeTask)
                .withEnvironmentVariables(environmentVariables)
        then:
        screenshotPreferencesByClass.screenshotPreference.isPresent() && screenshotPreferencesByClass.screenshotPreference.get() == TakeScreenshots.DISABLED
    }

    def "should override the screenshot configuration based on the plural form of the name of a class"() {
        given:
            environmentVariables.setProperty("serenity.take.screenshots.for.actions","disabled")
        when:
            ScreenshotPreferencesByClass screenshotPreferencesByClass = ScreenshotPreferencesByClass.forClass(SomeAction)
                    .withEnvironmentVariables(environmentVariables)
        then:
            screenshotPreferencesByClass.screenshotPreference.isPresent() && screenshotPreferencesByClass.screenshotPreference.get() == TakeScreenshots.DISABLED
    }


    def "should override the screenshot configuration based on the specific form of the name of a class"() {
        given:
        environmentVariables.setProperty("serenity.take.screenshots.for.someaction","disabled")
        when:
        ScreenshotPreferencesByClass screenshotPreferencesByClass = ScreenshotPreferencesByClass.forClass(SomeAction)
                .withEnvironmentVariables(environmentVariables)
        then:
        screenshotPreferencesByClass.screenshotPreference.isPresent() && screenshotPreferencesByClass.screenshotPreference.get() == TakeScreenshots.DISABLED
    }

    def "should report no configuration for unconfigured classes"() {
        given:
            environmentVariables.setProperty("serenity.take.screenshots.for.actions","disabled")
        when:
            ScreenshotPreferencesByClass screenshotPreferencesByClass = ScreenshotPreferencesByClass.forClass(SomeOtherClass)
                    .withEnvironmentVariables(environmentVariables)
        then:
            !screenshotPreferencesByClass.screenshotPreference.isPresent()
    }

    def "should report an error if not configured correctly"() {
        given:
            environmentVariables.setProperty("serenity.take.screenshots.for.actions","foo")
        when:
            ScreenshotPreferencesByClass screenshotPreferencesByClass = ScreenshotPreferencesByClass.forClass(SomeOtherClass)
                    .withEnvironmentVariables(environmentVariables)
        then:
            IllegalArgumentException exception = thrown()
            exception.message.contains("Incorrectly configured screenshot value 'foo' for serenity.take.screenshots")
    }
}

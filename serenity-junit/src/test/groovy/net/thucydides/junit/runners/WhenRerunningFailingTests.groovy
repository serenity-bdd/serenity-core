package net.thucydides.junit.runners

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.configuration.WebDriverConfiguration
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.core.webdriver.WebDriverFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files

class WhenRerunningFailingTests extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def webDriverFactory = new WebDriverFactory(environmentVariables)
    File temporaryDirectory

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile()
        temporaryDirectory.deleteOnExit()
        environmentVariables.setProperty("rerun.failures.directory",System.getProperty("user.dir") + File.separator + "src/test/resources/rerun")
        environmentVariables.setProperty("replay.failures","true")
    }


    @RunWith(SerenityRunner)
    static class ATestWithMoreTestMethods {

        @Test
        void testMethod1() {
            throw new Exception()
        }

        @Test
        void testMethod2() {
        }

        @Test
        void testMethod3() {
        }
    }

    @Ignore
    def "should rerun only tests specified in rerun file"() {
        given:
            def runner = new SerenityRunner(ATestWithMoreTestMethods,webDriverFactory, new WebDriverConfiguration(environmentVariables))
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.size() == 2
    }


}

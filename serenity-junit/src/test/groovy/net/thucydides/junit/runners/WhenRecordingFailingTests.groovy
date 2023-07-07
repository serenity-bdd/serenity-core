package net.thucydides.junit.runners

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.configuration.WebDriverConfiguration
import net.thucydides.core.environment.MockEnvironmentVariables
import net.thucydides.core.webdriver.WebDriverFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class WhenRecordingFailingTests extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def webDriverFactory = new WebDriverFactory(environmentVariables)
    File temporaryDirectory
    File rerunDir;

    def setup() {
        temporaryDirectory = Files.createTempDirectory("tmp").toFile();
        temporaryDirectory.deleteOnExit();
        rerunDir = Files.createTempDirectory("reruns").toFile();
        rerunDir.deleteOnExit()

        environmentVariables.setProperty("rerun.failures.directory", rerunDir.getCanonicalPath())
        environmentVariables.setProperty("record.failures","true")
    }

    @RunWith(SerenityRunner)
    static class ATestWithMoreTestMethods {

        @Test
        public void testMethod1() {
            throw new Exception();
        }

        @Test
        public void testMethod2() {
            throw new Exception();
        }

        @Test
        public void testMethod3() {
        }
    }

    def "should record failed tests in rerun file"() {
        given:
            def runner = new SerenityRunner(ATestWithMoreTestMethods,webDriverFactory,
                                            new WebDriverConfiguration(environmentVariables))
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.size() == 3

            def rerunFile = rerunDir.list()[0]
            String fileContents = Paths.get(rerunDir.getCanonicalPath(), rerunFile).toFile().text
            fileContents.contains("\"className\" : \"net.thucydides.junit.runners.WhenRecordingFailingTests.ATestWithMoreTestMethods\"")
    }


}

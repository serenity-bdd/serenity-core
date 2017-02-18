package net.thucydides.junit.runners

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.WebDriverFactory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

class WhenRecordingFailingTests extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def webDriverFactory = new WebDriverFactory(environmentVariables)
    File temporaryDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        temporaryDirectory = temporaryFolder.newFolder()
        environmentVariables.setProperty("rerun.failures.directory", temporaryFolder.getRoot().getCanonicalPath() + File.separator + "rerun")
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
            def runner = new SerenityRunner(ATestWithMoreTestMethods,webDriverFactory, new SystemPropertiesConfiguration().withEnvironmentVariables(environmentVariables))
        when:
            runner.run(new RunNotifier())
        then:
            runner.testOutcomes.size() == 3
            String fileContents = new File(temporaryFolder.getRoot().getCanonicalPath() + File.separator + "rerun" +  File.separator + "net.thucydides.junit.runners.WhenRecordingFailingTests.ATestWithMoreTestMethods_rerun.xml").text.trim()
            fileContents == '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<rerunnableClass>
    <className>net.thucydides.junit.runners.WhenRecordingFailingTests.ATestWithMoreTestMethods</className>
    <methodName>testMethod2</methodName>
    <methodName>testMethod1</methodName>
</rerunnableClass>
'''.trim()
    }


}

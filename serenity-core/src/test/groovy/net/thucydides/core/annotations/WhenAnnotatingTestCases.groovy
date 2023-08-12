package net.thucydides.core.annotations

import net.serenitybdd.annotations.Managed
import org.openqa.selenium.WebDriver
import spock.lang.Shared
import spock.lang.Specification

class WhenAnnotatingTestCases extends Specification {


    def setup() {
    }

    class AnnotatedTestClass {
        @Managed
        private WebDriver driver;
    }

    class AnnotatedTestClassWithUniqueSession {
        @Managed(uniqueSession = true)
        private WebDriver driver;
    }
    class AnnotatedParentTestClass {
        @Managed
        private WebDriver driver;
    }

    class AnnotatedChildClass extends AnnotatedParentTestClass {

    }

    def "should identify a test case or test step with the @Managed annotation"() {
        expect:
            TestCaseAnnotations.supportsWebTests(testclass) == expectedResult
        where:
            testclass           | expectedResult
            AnnotatedTestClass  | true
            AnnotatedChildClass | true
    }

    @Shared
    def annotatedTestClass = new AnnotatedTestClass();
    @Shared
    def annotatedTestClassWithUniqueSession = new AnnotatedTestClassWithUniqueSession();

    def "should identify test cases requesting a unique browser session"() {
        expect:
            TestCaseAnnotations.forTestCase(testObject).isUniqueSession() == uniqueSession
        where:
            testObject                          | uniqueSession
            annotatedTestClass                  | false
            annotatedTestClassWithUniqueSession | true
    }

    def "should inject webdriver instance into test objects"() {

        given:
            def driver = Mock(WebDriver)
        when:
            TestCaseAnnotations.forTestCase(testObject).injectDriver(driver)
        then:
            testObject.driver == driver
        where:
            testObject                           | isUniqueSession
            annotatedTestClass                  | false
            annotatedTestClassWithUniqueSession | true
    }

}

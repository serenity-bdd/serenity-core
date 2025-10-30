package net.thucydides.core.steps

import net.serenitybdd.annotations.Step
import net.serenitybdd.core.pages.PageObject
import net.thucydides.core.pages.Pages
import net.thucydides.core.webdriver.DriverConfiguration
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.webdriver.Configuration
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenInstanciatingStepLibraries extends Specification {

    def driver = Mock(WebDriver)
    def pages = new Pages(driver)
    def stepFactory = new StepFactory(pages)

    static class MyPageObject extends PageObject {

        MyPageObject(WebDriver driver) {
            super(driver)
        }
    }


    static class MySimplePageObject extends PageObject {
    }

    static class MyOtherPageObject extends PageObject {

        MyOtherPageObject() {
            super(null)
        }
    }

    static MyOtherPageObject myOtherPageObject = new MyOtherPageObject()

    static class MyStepLibrary extends ScenarioSteps {

        MyPageObject myPageObject
        MyOtherPageObject myInstantiatedPageObject
        EnvironmentVariables environmentVariables
        Configuration configuration
        DriverConfiguration driverConfiguration

        MyStepLibrary(Pages pages) {
            super(pages)
            myInstantiatedPageObject = myOtherPageObject
        }

        @Step
        def aStep() {}
    }

    static class MySimpleStepLibrary extends ScenarioSteps {

        MySimplePageObject myPageObject

        MySimpleStepLibrary(Pages pages) {
            super(pages)
        }

        @Step
        def aStep() {}

        @Step
        protected boolean aProtectedStep() { return true }
    }

    static class MyExtraSimpleStepLibrary extends ScenarioSteps {

        @Step
        def aStep() {}
    }


    def "the step factory should provide new instantiated step libraries"() {
        when:
            def myStepLibrary = stepFactory.getSharedStepLibraryFor(MyStepLibrary)
        then:
            myStepLibrary != null
    }

    def "should instantiate any uninitialized page objects in a step class"() {
        when:
            def myStepLibrary = stepFactory.getSharedStepLibraryFor(MyStepLibrary)
        then:
            myStepLibrary.myPageObject != null
    }

    def "should instantiate any uninitialized page objects in a step class with no page constructor"() {
        when:
            def myStepLibrary = stepFactory.getSharedStepLibraryFor(MyExtraSimpleStepLibrary)
        then:
            myStepLibrary.getPages() != null
    }

    def "should instantiate any uninitialized simplified page objects in a step class"() {
        when:
            def myStepLibrary = stepFactory.getSharedStepLibraryFor(MySimpleStepLibrary)
        then:
            myStepLibrary.myPageObject != null
        and:
            myStepLibrary.myPageObject.driver != null
    }


    def "should not instantiate page objects that are already instantiated"() {
        when:
            def myStepLibrary = stepFactory.getSharedStepLibraryFor(MyStepLibrary)
        then:
            myStepLibrary.myInstantiatedPageObject == myOtherPageObject
    }

    def "should instantiate environment variable fields"() {
        when:
        def myStepLibrary = stepFactory.getSharedStepLibraryFor(MyStepLibrary)
        then:
        myStepLibrary.environmentVariables != null
    }

    def "should instantiate system configuration variable fields"() {
        when:
        def myStepLibrary = stepFactory.getSharedStepLibraryFor(MyStepLibrary)
        then:
        myStepLibrary.configuration != null
    }

    def "should instantiate driver configuration variable fields"() {
        when:
        def myStepLibrary = stepFactory.getSharedStepLibraryFor(MyStepLibrary)
        then:
        myStepLibrary.driverConfiguration != null
    }

    def "should instantiate unique step libraries if requested"() {
        when:
            def aStepLibrary = stepFactory.getUniqueStepLibraryFor(MyStepLibrary)
        and:
            def anotherStepLibrary = stepFactory.getUniqueStepLibraryFor(MyStepLibrary)
        then:
            aStepLibrary != anotherStepLibrary
    }


    static class MyImmutableStepLibrary {

        private final String favoriteColor
        private final Integer favoriteNumber

        String getFavoriteColor() {
            return favoriteColor
        }

        Integer getFavoriteNumber() {
            return favoriteNumber
        }

        MyImmutableStepLibrary(String favoriteColor, Integer favoriteNumber) {
            this.favoriteColor = favoriteColor
            this.favoriteNumber = favoriteNumber
        }
    }

    def "should be able to instantiate step libraries with parameters in constructors"() {
        when:
            def immutableStepLibrary = stepFactory.getUniqueStepLibraryFor(MyImmutableStepLibrary, "red", 42)
        then:
            immutableStepLibrary.favoriteColor == "red" && immutableStepLibrary.favoriteNumber == 42

    }
}

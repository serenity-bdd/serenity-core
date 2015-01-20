package net.serenitybdd.core.annotations.locators

import io.appium.java_client.MobileBy
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.AndroidFindBy
import net.thucydides.core.webdriver.MobilePlatform
import spock.lang.Specification

class WhenUsingTheAppiumAndroidAnnotations extends Specification {

    class AnnotatedAppiumAndroidPageSample {


        @AndroidFindBy(accessibility = "accessibility")
        public MobileElement byAccessibilityId;

        @AndroidFindBy(uiAutomator = "uiAutomator")
        public MobileElement byAndroidUIAutomator;

    }

    def "should find the correct By class"() {

        given:
            def annotations = new SmartAnnotations(field, MobilePlatform.ANDROID)
        when:
            def by = annotations.buildBy()
        then:
            by.class == expectedType
        where:
            field                                                                   | expectedType
            AnnotatedAppiumAndroidPageSample.class.getField("byAccessibilityId")    | MobileBy.ByAccessibilityId
            AnnotatedAppiumAndroidPageSample.class.getField("byAndroidUIAutomator") | MobileBy.ByAndroidUIAutomator

    }

}

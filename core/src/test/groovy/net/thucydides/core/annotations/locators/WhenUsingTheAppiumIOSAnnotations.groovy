package net.thucydides.core.annotations.locators

import io.appium.java_client.MobileBy
import io.appium.java_client.MobileElement
import io.appium.java_client.pagefactory.iOSFindBy
import net.thucydides.core.webdriver.MobilePlatform
import spock.lang.Specification

class WhenUsingTheAppiumIOSAnnotations extends Specification {

    class AnnotatedAppiumiOSPageSample {

        @iOSFindBy(accessibility = "accessibility")
        public MobileElement byAccessibilityId;

        @iOSFindBy(uiAutomator = "uiAutomator")
        public MobileElement byIosUIAutomation;

    }

    def "should find the correct By class"() {

        given:
        def annotations = new SmartAnnotations(field, MobilePlatform.IOS)
        when:
        def by = annotations.buildBy()
        then:
        by.class == expectedType
        where:
        field                                                            | expectedType
        AnnotatedAppiumiOSPageSample.class.getField("byAccessibilityId") | MobileBy.ByAccessibilityId
        AnnotatedAppiumiOSPageSample.class.getField("byIosUIAutomation") | MobileBy.ByIosUIAutomation

    }

}

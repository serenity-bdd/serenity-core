package net.serenitybdd.core.annotations.locators

import io.appium.java_client.MobileBy
import net.serenitybdd.core.annotations.findby.FindBy
import net.serenitybdd.core.annotations.findby.How
import net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationProviderService
import net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationService
import net.thucydides.core.webdriver.MobilePlatform
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ByIdOrName
import spock.lang.Specification

class WhenUsingTheSmartAnnotations extends Specification {

    class AnnotatedPageSample {

        @FindBy(id = "someId")
        public WebElement byId;

        @FindByCustom
        public WebElement byCustom1;

        public WebElement byIdOrName;

        @FindBy(css = "someCss")
        public WebElement byCss;

        @FindBy(xpath = "someXpath")
        public WebElement byXpath;

        @FindBy(name = "someName")
        public WebElement byName;

        @FindBy(ngModel = "someModelName")
        public WebElement byNgModelName;

        @FindBy(linkText = "linkText")
        public WebElement byLinkText;

        @FindBy(partialLinkText = "partialLinkText")
        public WebElement byPartialLinkText;

        @FindBy(tagName = "tag")
        public WebElement byTagname;

        @FindBy(sclocator = "sclocator")
        public WebElement bySCLocator;

        @FindBy(jquery = "jquery")
        public WebElement byJQuery;

        @FindBy(className = "className")
        public WebElement byClassName;

        @FindBy(accessibilityId = "accessibilityId")
        public WebElement byAccessibilityId;

        @FindBy(iOSUIAutomation = "IOSUIAutomation")
        public WebElement byIOSUIAutomation;

        @FindBy(androidUIAutomator = "androidUIAutomator")
        public WebElement byAndroidUIAutomator;

        @FindBy(how = How.ID, using = "foo")
        public WebElement byIdLong;

        @FindBy(how = How.ID_OR_NAME, using = "foo")
        public WebElement byIdOrNameLong;

        @FindBy(how = How.CSS, using = "foo")
        public WebElement byCssLong;

        @FindBy(how = How.XPATH, using = "foo")
        public WebElement byXpathLong;

        @FindBy(how = How.NAME, using = "foo")
        public WebElement byNameLong;

        @FindBy(how = How.LINK_TEXT, using = "foo")
        public WebElement byLinkTextLong;

        @FindBy(how = How.PARTIAL_LINK_TEXT, using = "foo")
        public WebElement byPartialLinkTextLong;

        @FindBy(how = How.TAG_NAME, using = "foo")
        public WebElement byTagnameLong;

        @FindBy(how = How.SCLOCATOR, using = "foo")
        public WebElement bySCLocatorLong;

        @FindBy(how = How.JQUERY, using = "foo")
        public WebElement byJQueryLong;

        @FindBy(how = How.CLASS_NAME, using = "foo")
        public WebElement byClassNameLong;

        @FindBy(how = How.ACCESSIBILITY_ID, using = "foo")
        public WebElement byAccessibilityIdLong;

        @FindBy(how = How.IOS_UI_AUTOMATION, using = "foo")
        public WebElement byIOSUIAutomationLong;

        @FindBy(how = How.ANDROID_UI_AUTOMATOR, using = "foo")
        public WebElement byAndroidUIAutomatorLong;

        @FindByCustom
        public WebElement byCustom2;
    }

    class ByReact extends By {

        private static final char DOUBLE_QUOTE = '"';
        private static final String SINGLE_QUOTE = "'";

        private final String reactSelector;

        public ByReact(String reactSelector){
            this.reactSelector = reactSelector;
        }

        @Override
        public WebElement findElement(SearchContext context) {
            String jquery = "return __retractor(" + quoted(reactSelector) + ")[0];";
            return (WebElement) ((JavascriptExecutor) context).executeScript(jquery);
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            String jquery = "return __retractor(" + quoted(reactSelector) + ");";
            return (List<WebElement>) ((JavascriptExecutor) context).executeScript(jquery);
        }

        private String quoted(final String reactSelector) {
            if (reactSelector.contains("'")) {
                return DOUBLE_QUOTE + reactSelector + '"';
            } else {
                return  "'" + reactSelector + SINGLE_QUOTE;
            }
        }

        public String toString() {
            return "By.ByReact: " + reactSelector;
        }
    }

    def "should find the correct By class"() {

        given:
        def annotations = new SmartAnnotations(field, MobilePlatform.NONE)
        when:
        def by = annotations.buildBy()
        then:
        by.class == expectedType
        where:
        field                                                      | expectedType
        AnnotatedPageSample.class.getField("byId")                 | By.ById
        AnnotatedPageSample.class.getField("byIdOrName")           | ByIdOrName
        AnnotatedPageSample.class.getField("byCss")                | By.ByCssSelector
        AnnotatedPageSample.class.getField("byName")               | By.ByName
        AnnotatedPageSample.class.getField("byClassName")          | By.ByClassName
        AnnotatedPageSample.class.getField("byLinkText")           | By.ByLinkText
        AnnotatedPageSample.class.getField("byPartialLinkText")    | By.ByPartialLinkText
        AnnotatedPageSample.class.getField("byXpath")              | By.ByXPath
        AnnotatedPageSample.class.getField("byTagname")            | By.ByTagName
        AnnotatedPageSample.class.getField("byJQuery")             | net.serenitybdd.core.annotations.findby.By.ByjQuerySelector
        AnnotatedPageSample.class.getField("bySCLocator")          | net.serenitybdd.core.annotations.findby.By.ByScLocator
        AnnotatedPageSample.class.getField("byNgModelName")        | By.ByCssSelector
        AnnotatedPageSample.class.getField("byAccessibilityId")    | MobileBy.ByAccessibilityId
//        AnnotatedPageSample.class.getField("byIOSUIAutomation")    | MobileBy.ByIosUIAutomation
        AnnotatedPageSample.class.getField("byAndroidUIAutomator") | MobileBy.ByAndroidUIAutomator
    }


    def "should find the correct By class using long ID"() {

        given:
        def annotations = new SmartAnnotations(field, MobilePlatform.NONE)
        when:
        def by = annotations.buildBy()
        then:
        by.class == expectedType
        where:
        field                                                          | expectedType
        AnnotatedPageSample.class.getField("byIdLong")                 | By.ById
        AnnotatedPageSample.class.getField("byIdOrNameLong")           | ByIdOrName
        AnnotatedPageSample.class.getField("byCssLong")                | By.ByCssSelector
        AnnotatedPageSample.class.getField("byNameLong")               | By.ByName
        AnnotatedPageSample.class.getField("byClassNameLong")          | By.ByClassName
        AnnotatedPageSample.class.getField("byLinkTextLong")           | By.ByLinkText
        AnnotatedPageSample.class.getField("byPartialLinkTextLong")    | By.ByPartialLinkText
        AnnotatedPageSample.class.getField("byXpathLong")              | By.ByXPath
        AnnotatedPageSample.class.getField("byTagnameLong")            | By.ByTagName
        AnnotatedPageSample.class.getField("byJQueryLong")             | net.serenitybdd.core.annotations.findby.By.ByjQuerySelector
        AnnotatedPageSample.class.getField("bySCLocatorLong")          | net.serenitybdd.core.annotations.findby.By.ByScLocator
        AnnotatedPageSample.class.getField("byAccessibilityIdLong")    | MobileBy.ByAccessibilityId
//        AnnotatedPageSample.class.getField("byIOSUIAutomationLong")    | MobileBy.ByIosUIAutomation
        AnnotatedPageSample.class.getField("byAndroidUIAutomatorLong") | MobileBy.ByAndroidUIAutomator
    }

    def "should find the correct cutom By class"() {
        CustomFindByAnnotationProviderService customFindByAnnotationProviderService = Mock()
        CustomFindByAnnotationService customFindByAnnotationService = Mock()

        given:
        By expectedBy = new ByReact("test")
        customFindByAnnotationService.isAnnotatedByCustomFindByAnnotation(field) >> true
        customFindByAnnotationService.buildByFromCustomFindByAnnotation(field) >> expectedBy
        customFindByAnnotationProviderService.customFindByAnnotationServices>>[customFindByAnnotationService]
        def annotations = new SmartAnnotations(field, MobilePlatform.NONE, customFindByAnnotationProviderService)
        when:
        def by = annotations.buildBy()
        then:
        by.class == expectedType
        where:
        field                                                                            | expectedType
        AnnotatedPageSample.class.getField("byId")               | By.ById
        AnnotatedPageSample.class.getField("byCustom1")     | ByReact
        AnnotatedPageSample.class.getField("byCustom2")     | ByReact
    }

}

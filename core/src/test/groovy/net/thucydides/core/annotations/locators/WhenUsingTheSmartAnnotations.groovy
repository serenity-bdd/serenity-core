package net.thucydides.core.annotations.locators

import net.thucydides.core.annotations.findby.FindBy
import net.thucydides.core.annotations.findby.How
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ByIdOrName
import spock.lang.Specification

class WhenUsingTheSmartAnnotations extends Specification {

    class AnnotatedPageSample {

        @FindBy(id="someId")
        public WebElement byId;

        public WebElement byIdOrName;

        @FindBy(css="someCss")
        public WebElement byCss;

        @FindBy(xpath="someXpath")
        public WebElement byXpath;

        @FindBy(name ="someName")
        public WebElement byName;

        @FindBy(ngModel="someModelName")
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


        @FindBy(how = How.ID, using="foo")
        public WebElement byIdLong;

        @FindBy(how = How.ID_OR_NAME, using="foo")
        public WebElement byIdOrNameLong;

        @FindBy(how = How.CSS, using="foo")
        public WebElement byCssLong;

        @FindBy(how = How.XPATH, using="foo")
        public WebElement byXpathLong;

        @FindBy(how = How.NAME, using="foo")
        public WebElement byNameLong;

        @FindBy(how = How.LINK_TEXT, using="foo")
        public WebElement byLinkTextLong;

        @FindBy(how = How.PARTIAL_LINK_TEXT, using="foo")
        public WebElement byPartialLinkTextLong;

        @FindBy(how = How.TAG_NAME, using="foo")
        public WebElement byTagnameLong;

        @FindBy(how = How.SCLOCATOR, using="foo")
        public WebElement bySCLocatorLong;

        @FindBy(how = How.JQUERY, using="foo")
        public WebElement byJQueryLong;

        @FindBy(how = How.CLASS_NAME, using="foo")
        public WebElement byClassNameLong;

    }

    def "should find the correct By class"() {

        given:
            def annotations = new SmartAnnotations(field)
        when:
            def by = annotations.buildBy()
        then:
            by.class == expectedType
        where:
        field                                                   | expectedType
        AnnotatedPageSample.class.getField("byId")              | By.ById
        AnnotatedPageSample.class.getField("byIdOrName")        | ByIdOrName
        AnnotatedPageSample.class.getField("byCss")             | By.ByCssSelector
        AnnotatedPageSample.class.getField("byName")            | By.ByName
        AnnotatedPageSample.class.getField("byClassName")       | By.ByClassName
        AnnotatedPageSample.class.getField("byLinkText")        | By.ByLinkText
        AnnotatedPageSample.class.getField("byPartialLinkText") | By.ByPartialLinkText
        AnnotatedPageSample.class.getField("byXpath")           | By.ByXPath
        AnnotatedPageSample.class.getField("byTagname")         | By.ByTagName
        AnnotatedPageSample.class.getField("byJQuery")          | net.thucydides.core.annotations.findby.By.ByjQuerySelector
        AnnotatedPageSample.class.getField("bySCLocator")       | net.thucydides.core.annotations.findby.By.ByScLocator
        AnnotatedPageSample.class.getField("byNgModelName")     | By.ByCssSelector
    }


    def "should find the correct By class using long ID"() {

        given:
        def annotations = new SmartAnnotations(field)
        when:
        def by = annotations.buildBy()
        then:
        by.class == expectedType
        where:
        field                                               | expectedType
        AnnotatedPageSample.class.getField("byIdLong")          | By.ById
        AnnotatedPageSample.class.getField("byIdOrNameLong")    | ByIdOrName
        AnnotatedPageSample.class.getField("byCssLong")         | By.ByCssSelector
        AnnotatedPageSample.class.getField("byNameLong")        | By.ByName
        AnnotatedPageSample.class.getField("byClassNameLong")   | By.ByClassName
        AnnotatedPageSample.class.getField("byLinkTextLong")    | By.ByLinkText
        AnnotatedPageSample.class.getField("byPartialLinkTextLong")   | By.ByPartialLinkText
        AnnotatedPageSample.class.getField("byXpathLong")       | By.ByXPath
        AnnotatedPageSample.class.getField("byTagnameLong")     | By.ByTagName
        AnnotatedPageSample.class.getField("byJQueryLong")      | net.thucydides.core.annotations.findby.By.ByjQuerySelector
        AnnotatedPageSample.class.getField("bySCLocatorLong")   | net.thucydides.core.annotations.findby.By.ByScLocator
    }


}

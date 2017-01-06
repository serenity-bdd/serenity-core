package net.thucydides.core.pages

import net.serenitybdd.core.pages.PageUrls
import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.annotations.NamedUrl
import net.thucydides.core.annotations.NamedUrls
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import spock.lang.Specification
import spock.lang.Unroll

class WhenConfiguringTheStartingUrlForAPage extends Specification {

    static class UnannotatedPageObject {}

    @DefaultUrl("http://default.url")
    static class PageObjectWithDefaultUrl {}

    @DefaultUrl("http://default.url/some/path")
    static class PageObjectWithRelativeDefaultUrl {}

    @DefaultUrl("/relative/path")
    static class PageObjectWithPureRelativeDefaultUrl {}

    @DefaultUrl("htp://dodgy.url")
    static class PageObjectWithDodgyUrl {}

    @DefaultUrl("http://default.url/#MyPage")
    static class PageObjectWithHashNotation {}

    @DefaultUrl("http://default.url:9090/some/path")
    static class PageObjectWithDefaultUrlWithPort {}


    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    static def unannotatedPageObject = new UnannotatedPageObject()
    static def pageObjectWithDefaultUrl = new PageObjectWithDefaultUrl()
    static def pageObjectWithRelativeDefaultUrl = new PageObjectWithRelativeDefaultUrl()
    static def pageObjectWithRelativePath = new PageObjectWithPureRelativeDefaultUrl()
    static def pageObjectWithDodgyUrl = new PageObjectWithDodgyUrl()
    static def pageObjectWithHashNotation = new PageObjectWithHashNotation()
    static def pageObjectWithDefaultUrlWithPort = new PageObjectWithDefaultUrlWithPort()

    @Unroll
    def "Should use the default URL and the webdriver.base.url property to determine the starting URL"() {
        given:
            if (webdriverBaseUrl != null) {
                environmentVariables.setProperty("webdriver.base.url",webdriverBaseUrl)
            }
        when:
            def configuration = new SystemPropertiesConfiguration(environmentVariables)
            def pageUrls = new PageUrls(pageObject, configuration)

        then:
            pageUrls.startingUrl == expectedStartingUrl
        where:
        webdriverBaseUrl                  | pageObject                       | expectedStartingUrl
        ""                                | pageObjectWithDefaultUrl         | "http://default.url"
        "http://system.provided.url"      | unannotatedPageObject            | "http://system.provided.url"
        null                              | pageObjectWithDefaultUrl         | "http://default.url"
        "http://system.provided.url"      | pageObjectWithDefaultUrl         | "http://system.provided.url"
        null                              | pageObjectWithRelativeDefaultUrl | "http://default.url/some/path"
        ""                                | pageObjectWithRelativeDefaultUrl | "http://default.url/some/path"
        "http://system.provided.url"      | pageObjectWithRelativeDefaultUrl | "http://system.provided.url/some/path"
        "http://system.provided.url:8080" | pageObjectWithRelativeDefaultUrl | "http://system.provided.url:8080/some/path"
        "http://system.provided.url"      | pageObjectWithRelativePath       | "http://system.provided.url/relative/path"
        "http://system.provided.url:8080" | pageObjectWithRelativePath       | "http://system.provided.url:8080/relative/path"
        ""                                | pageObjectWithHashNotation       | "http://default.url/#MyPage"
        null                              | pageObjectWithHashNotation       | "http://default.url/#MyPage"
        "http://system.provided.url"      | pageObjectWithHashNotation       | "http://system.provided.url/#MyPage"
        "http://system.provided.url:8080" | pageObjectWithHashNotation       | "http://system.provided.url:8080/#MyPage"
        null                              | pageObjectWithDefaultUrlWithPort | "http://default.url:9090/some/path"
        "http://system.provided.url"      | pageObjectWithDefaultUrlWithPort | "http://system.provided.url/some/path"
        "http://system.provided.url:8080" | pageObjectWithDefaultUrlWithPort | "http://system.provided.url:8080/some/path"
    }

    @DefaultUrl("classpath:static-site/index.html")
    static class PageObjectUsingAClasspath {}
    static def pageObjectUsingAClasspath = new PageObjectUsingAClasspath()

    @Unroll
    def "Should be able to define a classpath resource as a default URL"() {
        given:
            if (webdriverBaseUrl != null) {
                environmentVariables.setProperty("webdriver.base.url",webdriverBaseUrl)
            }
        when:
            def configuration = new SystemPropertiesConfiguration(environmentVariables)
            def pageUrls = new PageUrls(pageObject, configuration)
            def startingUrl = pageUrls.startingUrl
        then:
            startingUrl.startsWith(startsWith) && startingUrl.endsWith(endsWith)
        where:
        webdriverBaseUrl                                | pageObject                  | startsWith | endsWith
        ""                                              | pageObjectUsingAClasspath   | "file:/"   | "static-site/index.html"
        null                                            | pageObjectUsingAClasspath   | "file:/"   | "static-site/index.html"
        "http://system.provided.url"                    | pageObjectUsingAClasspath   | "http://"  | "http://system.provided.url"
        "classpath:static-site/alternative-index.html"  | pageObjectUsingAClasspath   | "file:/"   | "static-site/alternative-index.html"

    }

    @Unroll
    def "Should also be able to define the default base URL from the Pages factory"() {
        given:
            if (webdriverBaseUrl) {
                environmentVariables.setProperty("webdriver.base.url",webdriverBaseUrl)
            }
        when:
            def configuration = new SystemPropertiesConfiguration(environmentVariables)
            def pageUrls = new PageUrls(pageObject, configuration)
            if (pagesDefaultUrl) {
                pageUrls.overrideDefaultBaseUrl(pagesDefaultUrl)
            }

        then:
        pageUrls.startingUrl == expectedStartingUrl
        where:
        webdriverBaseUrl                  | pagesDefaultUrl                  | pageObject                       | expectedStartingUrl
        "http://system.provided.url"      | ""                               | unannotatedPageObject            | "http://system.provided.url"
        ""                                | "http://pages.url"               | unannotatedPageObject            | "http://pages.url"
        "http://system.provided.url"      | "http://pages.url"               | unannotatedPageObject            | "http://system.provided.url"
        "http://system.provided.url"      | ""                               | pageObjectWithDefaultUrl         | "http://system.provided.url"
        ""                                | "http://pages.url"               | pageObjectWithDefaultUrl         | "http://pages.url"
        "http://system.provided.url"      | "http://pages.url"               | pageObjectWithDefaultUrl         | "http://system.provided.url"
        "http://system.provided.url"      | ""                               | pageObjectWithRelativeDefaultUrl | "http://system.provided.url/some/path"
        ""                                | "http://pages.url"               | pageObjectWithRelativeDefaultUrl | "http://pages.url/some/path"
        "http://system.provided.url"      | "http://pages.url"               | pageObjectWithRelativeDefaultUrl | "http://system.provided.url/some/path"
    }

    @Unroll
    def "should raise an exception if a url is dodgy"() {
        given:
            if (webdriverBaseUrl) {
                environmentVariables.setProperty("webdriver.base.url",webdriverBaseUrl)
            }
            def configuration = new SystemPropertiesConfiguration(environmentVariables)
            def pageUrls = new PageUrls(pageObject, configuration)
            if (pagesDefaultUrl) {
                pageUrls.overrideDefaultBaseUrl(pagesDefaultUrl)
            }
        when:
            pageUrls.startingUrl

        then:
            AssertionError e = thrown()
            e.message == errorMessage

        where:
        webdriverBaseUrl       | pagesDefaultUrl                  | pageObject                         | errorMessage
        "htp://dodgy.url"      | ""                               | unannotatedPageObject              | "Invalid URL: htp://dodgy.url"
        "htp://dodgy.url"      | ""                               | pageObjectWithDefaultUrl           | "Invalid URL: htp://dodgy.url"
        "htp://dodgy.url"      | ""                               | pageObjectWithRelativeDefaultUrl   | "Invalid URL: htp://dodgy.url/some/path"
        "htp://dodgy.url"      | ""                               | pageObjectWithDodgyUrl             | "Invalid URL: htp://dodgy.url"
        ""                     | "htp://dodgy.url"                | unannotatedPageObject              | "Invalid URL: htp://dodgy.url"
        ""                     | "htp://dodgy.url"                | pageObjectWithDefaultUrl           | "Invalid URL: htp://dodgy.url"
        ""                     | "htp://dodgy.url"                | pageObjectWithRelativeDefaultUrl   | "Invalid URL: htp://dodgy.url/some/path"
        ""                     | "htp://dodgy.url"                | pageObjectWithDodgyUrl             | "Invalid URL: htp://dodgy.url"
        ""                     | ""                               | pageObjectWithRelativePath         | "Invalid URL: /relative/path"
    }

    @DefaultUrl("http://jira.mycompany.org")
    @NamedUrls([@NamedUrl(name = "open.issue", url = "/issues/{1}")])
    static class PageObjectWithParameters {}

    @DefaultUrl("http://jira.mycompany.org:8888")
    @NamedUrls([@NamedUrl(name = "open.issue", url = "/issues/{1}")])
    static class PageObjectWithParametersAndPort {}

    @DefaultUrl("http://jira.mycompany.org")
    @NamedUrls([@NamedUrl(name = "open.issue", url = "http://jira.mycompany.org/issues/{1}")])
    static class PageObjectWithParamsAndNonRelativePath {}

    static def pageObjectWithParameters = new PageObjectWithParameters()
    static def pageObjectWithParametersAndPort = new PageObjectWithParametersAndPort()
    static def pageObjectWithParamsAndFullPath = new PageObjectWithParamsAndNonRelativePath()

    @Unroll
    def "should inject parameters into named urls"() {
        given:
        if (webdriverBaseUrl) {
            environmentVariables.setProperty("webdriver.base.url",webdriverBaseUrl)
        }
        when:
            def configuration = new SystemPropertiesConfiguration(environmentVariables)
            def pageUrls = new PageUrls(pageObject, configuration)
        then:
            pageUrls.getNamedUrl("open.issue",*parameters) == expectedStartingUrl
        where:
        webdriverBaseUrl                  | pageObject                      | parameters   | expectedStartingUrl
        null                              | pageObjectWithParameters        | ["PROJ-123"] | "http://jira.mycompany.org/issues/PROJ-123"
        ""                                | pageObjectWithParameters        | ["PROJ-123"] | "http://jira.mycompany.org/issues/PROJ-123"
        "http://system.provided.url"      | pageObjectWithParameters        | ["PROJ-123"] | "http://system.provided.url/issues/PROJ-123"
        "http://system.provided.url:8080" | pageObjectWithParameters        | ["PROJ-123"] | "http://system.provided.url:8080/issues/PROJ-123"
        null                              | pageObjectWithParametersAndPort | ["PROJ-123"] | "http://jira.mycompany.org:8888/issues/PROJ-123"
        "http://system.provided.url"      | pageObjectWithParametersAndPort | ["PROJ-123"] | "http://system.provided.url/issues/PROJ-123"
        "http://system.provided.url:8080" | pageObjectWithParametersAndPort | ["PROJ-123"] | "http://system.provided.url:8080/issues/PROJ-123"
        null                              | pageObjectWithParamsAndFullPath | ["PROJ-123"] | "http://jira.mycompany.org/issues/PROJ-123"
        ""                                | pageObjectWithParamsAndFullPath | ["PROJ-123"] | "http://jira.mycompany.org/issues/PROJ-123"
        "http://system.provided.url"      | pageObjectWithParamsAndFullPath | ["PROJ-123"] | "http://system.provided.url/issues/PROJ-123"
        "http://system.provided.url:8080" | pageObjectWithParamsAndFullPath | ["PROJ-123"] | "http://system.provided.url:8080/issues/PROJ-123"
    }

    @DefaultUrl("http://jira.mycompany.org/issues/{1}")
    static class PageObjectWithAParameter {}

    def pageObjectWithAParameter = new PageObjectWithAParameter()

    @Unroll
    def "should inject parameters into the starting url"() {
        given:
            if (webdriverBaseUrl) {
                environmentVariables.setProperty("webdriver.base.url",webdriverBaseUrl)
            }
        when:
            def configuration = new SystemPropertiesConfiguration(environmentVariables)
            def pageUrls = new PageUrls(pageObjectWithAParameter, configuration)
        then:
            pageUrls.getStartingUrl(*parameters) == expectedStartingUrl
        where:
        webdriverBaseUrl                      | parameters   | expectedStartingUrl
        null                                  | ["PROJ-123"] | "http://jira.mycompany.org/issues/PROJ-123"
        ""                                    | ["PROJ-123"] | "http://jira.mycompany.org/issues/PROJ-123"
        "http://system.provided.url"          | ["PROJ-123"] | "http://system.provided.url/issues/PROJ-123"
        "http://system.provided.url:8080"     | ["PROJ-123"] | "http://system.provided.url:8080/issues/PROJ-123"
    }
}
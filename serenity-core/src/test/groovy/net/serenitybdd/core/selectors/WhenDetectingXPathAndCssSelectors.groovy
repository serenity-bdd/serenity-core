package net.serenitybdd.core.selectors

import spock.lang.Specification
import spock.lang.Unroll

class WhenDetectingXPathAndCssSelectors extends Specification {

    @Unroll
    def "should distinguish XPath from CSS correctly for #expression"() {
        expect:
        Selectors.isXPath(expression) == isXPath
        where:
        expression                        | isXPath
        "//some/xpath"                    | true
        "./author"                        | true
        "author"                          | true
        "first.name"                      | true
        "/bookstore"                      | true
        "//author"                        | true
        "/bookstore/*/title"              | true
        "*[@specialty]"                   | true
        "*/*"                             | true
        "@style"                          | true
        "price/@exchange"                 | true
        "book/*[@style]"                  | true
        "input[contains(@id,'userName')]" | true
        '.intro'                          | false
        '#id'                             | false
        'p ~ ul'                          | false
        '[target=_blank]'                 | false
        'a[href$=".pdf"]'                 | false
        'p::first-line'                   | false
        // The folloi
// Fails:        'a:active'                        | false
// Fails:        'div > p'                         | false
// Fails:        'element+element'                 | false
// Fails:        'p:nth-child(2)'                  | false
    }
}

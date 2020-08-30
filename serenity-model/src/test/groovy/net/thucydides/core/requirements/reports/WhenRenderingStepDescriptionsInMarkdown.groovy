package net.thucydides.core.requirements.reports


import spock.lang.Specification
import spock.lang.Unroll

class WhenRenderingStepDescriptionsInMarkdown extends Specification {

    def "Should convert simple tables to markdown format"() {
        given:
        def scenarioText = """
Given some data:
| A | B | C |
| 1 | 2 | 3 |
"""
        when:
        def renderedText = RenderMarkdown.convertEmbeddedTablesIn(scenarioText)
        then:
        renderedText == """
Given some data:


| A | B | C |
|---|---|---|
| 1 | 2 | 3 |
"""
    }

    def "Should convert multi-line tables to markdown format"() {
        given:
        def scenarioText = """
Given some data:
| A | B | C |
| 1 | 2 | 3 |
| 4 | 5 | 6 |
"""
        when:
        def renderedText = RenderMarkdown.convertEmbeddedTablesIn(scenarioText)
        then:
        renderedText == """
Given some data:


| A | B | C |
|---|---|---|
| 1 | 2 | 3 |
| 4 | 5 | 6 |
"""
    }
    @Unroll
    def "Text without any tables should not be converted"() {

        expect:
        RenderMarkdown.convertEmbeddedTablesIn(text) == markdownText
        where:
        text | markdownText
        ""   | ""
        "Given some apples and some pears" | "Given some apples and some pears"
    }

    def "Already rendered markdown tables should not be converted"() {
        given:
            def scenarioText = """
Given some data:


| A | B | C |
|---|---|---|
| 1 | 2 | 3 |
"""
        when:
            def renderedText = RenderMarkdown.convertEmbeddedTablesIn(scenarioText)
        then:
            renderedText == scenarioText
    }

    def "Should convert embedded tables"() {
        given:
        def scenarioTest = """Given a business with the following details:
|Name | Category|
|<Name> | <Category>|
When the business risk profile is assessed
Then the risk rating should be <Risk>"""
        when:
        def renderedText = RenderMarkdown.convertEmbeddedTablesIn(scenarioTest)
        then:
        renderedText == """Given a business with the following details:


|Name | Category|
|---|---|
|<Name> | <Category>|
When the business risk profile is assessed
Then the risk rating should be <Risk>
"""
    }



}


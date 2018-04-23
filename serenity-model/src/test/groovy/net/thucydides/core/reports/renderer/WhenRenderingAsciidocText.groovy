package net.thucydides.core.reports.renderer

import spock.lang.Specification
import spock.lang.Unroll

class WhenRenderingAsciidocText extends Specification {

    @Unroll
    def "should render asciidoc as HTML"() {
        given:
        def markupRenderer = new AsciidocMarkupRenderer();
        when:
        def renderedText = markupRenderer.render(text);
        then:
        renderedText == expectedText
        where:
        text               | expectedText
        "some *bold* text" | "some <strong>bold</strong> text"
        "some normal text" | "some normal text"
        ""                 | ""
        null               | ""
    }
}

package net.thucydides.core.reports.renderer

import spock.lang.Specification

class WhenRenderingAsciidocText extends Specification {

    def "should render asciidoc as HTML"() {
        given:
            def markupRenderer = new AsciidocMarkupRenderer();
        when:
            def renderedText = markupRenderer.render("some *bold* text");
        then:
            renderedText == "some <strong>bold</strong> text"
    }
}

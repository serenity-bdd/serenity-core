package net.serenitybdd.reports.asciidoc

import nl.jworks.markdown_to_asciidoc.Converter

class Formatted {
    fun asAsciidoc(markdownText:String) = Converter.convertMarkdownToAsciiDoc(markdownText)
}
package net.serenitybdd.reports.asciidoc

import com.sun.xml.internal.ws.util.StringUtils
import nl.jworks.markdown_to_asciidoc.Converter

class Formatted {
    fun asAsciidoc(markdownText:String) = Converter.convertMarkdownToAsciiDoc(markdownText)
    fun asATitle(text:String) = StringUtils.capitalize(text)
}
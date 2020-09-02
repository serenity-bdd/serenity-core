package net.thucydides.core.requirements.model;

import net.serenitybdd.core.strings.FirstLine;

import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class CustomFieldValue {

    private String name;
    private String text;
    private String renderedText;


    public CustomFieldValue() {
        // Used by Jackson
    }

    public CustomFieldValue(String name, String text) {
        this.name = name;
        this.text = text;
        this.renderedText = null;
    }

    public CustomFieldValue(String name, String text, String renderedText) {
        this.name = name;
        this.text = text;
        this.renderedText = renderedText;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getRenderedText() {
        return (renderedText != null) ? withLineBreaks(renderedText) : withLineBreaks(text);
    }

    public String getRenderedTextWithoutTables() {
        String fullText = (renderedText != null) ? withLineBreaks(renderedText) : withLineBreaks(text);
        return  fullText.replaceAll("\\{example-result.*\\}","");
    }

    private String withLineBreaks(String text) {
        return asList(text.split(("\\r?\\n"))).stream()
                .map(line -> line + "  ")
                .collect(Collectors.joining(System.lineSeparator()));
    }
    /**
     * Return the first paragraph of the rendered text.
     */
    public String getRenderedSummary() {
        String rawText =  (renderedText != null) ? renderedText : text;
        return FirstLine.of(rawText);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("name='").append(name).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", renderedText='").append(renderedText).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

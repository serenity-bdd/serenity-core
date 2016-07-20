package net.thucydides.core.requirements.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
        return (renderedText != null) ? renderedText : text;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("text", text)
                .append("renderedText", renderedText)
                .toString();
    }
}

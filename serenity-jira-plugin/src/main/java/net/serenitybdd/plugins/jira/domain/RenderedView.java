package net.serenitybdd.plugins.jira.domain;

import java.util.Map;
import java.util.Optional;

public class RenderedView {
    private static final String RENDERED_DESCRIPTION_FIELD = "Description";

    private final Map<String, String> renderedFieldValues;

    public RenderedView(Map<String, String> renderedFieldValues) {
        this.renderedFieldValues = renderedFieldValues;
    }

    public String getDescription() {
        return renderedFieldValues.containsKey(RENDERED_DESCRIPTION_FIELD) ?
                renderedFieldValues.get(RENDERED_DESCRIPTION_FIELD) : renderedFieldValues.get(RENDERED_DESCRIPTION_FIELD.toLowerCase());

    }

    public boolean hasField(String field) {
        return renderedFieldValues.containsKey(field);
    }

    public Optional<String> customField(String field) {
        return Optional.ofNullable(renderedFieldValues.get(field));
    }
}

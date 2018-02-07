package net.thucydides.core.steps;

import net.serenitybdd.core.strings.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.thucydides.core.annotations.Fields.FieldValue.UNDEFINED;

public class ReplaceField {
    private final String stepDescription;
    private final String field;

    public ReplaceField(String stepDescription, String field) {

        this.stepDescription = stepDescription;
        this.field = field;
    }

    public static ReplaceFieldBuilder in(String stepDescription) {
        return new ReplaceFieldBuilder(stepDescription);
    }

    public String with(Object value) {
        String fieldName = fieldNameFor(field);
        if (stepDescription.contains(fieldName) && (value != UNDEFINED)) {
            return StringUtils.replace(stepDescription, fieldNameFor(field), stringValueFor(value));
        } else {
            return stepDescription;
        }
    }

    public static class ReplaceFieldBuilder {

        private String stepDescription;

        public ReplaceFieldBuilder(String stepDescription) {

            this.stepDescription = stepDescription;
        }

        public ReplaceField theFieldCalled(String field) {
            return new ReplaceField(stepDescription, field);
        }
    }

    private String fieldNameFor(String field) {
        return "#" + field;
    }

    private String stringValueFor(Object value) {

        if (value == null) { return ""; }

        if (value instanceof Keys[]) {
            return keyNamesFor((Keys[]) value);
        }
        if (value.getClass().isArray()) {
            List<String> elements = new ArrayList<>();
            Collections.singletonList(value).forEach(
                    part -> elements.add(part.toString())
            );

            return Joiner.on(",").join(elements);
        }
        return value.toString();
    }

    private String keyNamesFor(Keys[] keyValues) {
        List<String> keyNames = new ArrayList<>();
        for(Keys keyValue: keyValues) {
            keyNames.add((keyValue.name()));
        }
        return Joiner.on(",").join(keyNames);
    }

}

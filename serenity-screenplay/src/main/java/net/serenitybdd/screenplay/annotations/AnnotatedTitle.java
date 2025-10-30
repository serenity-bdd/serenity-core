package net.serenitybdd.screenplay.annotations;

import net.serenitybdd.annotations.Fields;
import net.serenitybdd.screenplay.Uninstrumented;
import org.apache.commons.lang3.StringUtils;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.Field;
import java.util.Set;

public class AnnotatedTitle {

    public static AnnotatedTitle injectFieldsInto(String text) {
        return new AnnotatedTitle(text);
    }

    private final String text;


    private AnnotatedTitle(String text) {
        this.text = text;
    }

    public String using(Object question) {
        Set<Field> fields = Fields.of(Uninstrumented.versionOf(question.getClass())).allFields();
        String updatedText = text;
        for(Field field : fields) {
            String fieldName = fieldVariableFor(field.getName());
            Object value = getValueFrom(question, field);
            if (updatedText.contains(fieldName) && (value != null)) {
                updatedText = StringUtils.replace(updatedText, fieldName, value.toString());
            }
        }
        return updatedText;
    }

    private Object getValueFrom(Object question, Field field) {
        try {
            field.setAccessible(true);
            return field.get(question);
        } catch (IllegalAccessException e) {
            throw new AssertionFailedError("Question label could not be instantiated for "  + text);
        }
    }

    private String fieldVariableFor(String field) {
        return "#" + field;
    }
}

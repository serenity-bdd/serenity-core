package net.serenitybdd.screenplay.questions.converters.converters;

public class StringConverter implements Converter<String> {
    @Override
    public String convert(Object value) {
        return value != null ? value.toString() : "";
    }
}

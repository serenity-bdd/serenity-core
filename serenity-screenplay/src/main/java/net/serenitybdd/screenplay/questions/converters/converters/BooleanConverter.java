package net.serenitybdd.screenplay.questions.converters.converters;

public class BooleanConverter implements Converter<Boolean> {
    @Override
    public Boolean convert(Object value) {
        return Boolean.parseBoolean(value.toString().toLowerCase());
    }
}

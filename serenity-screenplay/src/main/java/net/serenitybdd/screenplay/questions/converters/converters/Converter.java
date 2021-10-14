package net.serenitybdd.screenplay.questions.converters.converters;

public interface Converter<TO> {
    TO convert(Object value);
}

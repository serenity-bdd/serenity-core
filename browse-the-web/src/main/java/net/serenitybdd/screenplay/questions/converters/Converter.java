package net.serenitybdd.screenplay.questions.converters;

public interface Converter<TO> {
    TO convert(Object value);
}

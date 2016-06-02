package net.serenitybdd.screenplay.questions.converters;

public class LongConverter implements Converter<Long> {
    @Override
    public Long convert(Object value) {
        return Long.parseLong(value.toString());
    }
}

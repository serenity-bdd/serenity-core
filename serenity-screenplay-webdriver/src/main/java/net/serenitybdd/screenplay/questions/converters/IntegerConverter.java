package net.serenitybdd.screenplay.questions.converters;

public class IntegerConverter implements Converter<Integer> {
    @Override
    public Integer convert(Object value) {
        return Integer.parseInt(value.toString());
    }
}

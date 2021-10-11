package net.serenitybdd.screenplay.questions.converters.converters;

public class DoubleConverter implements Converter<Double> {
    @Override
    public Double convert(Object value) {
        return Double.parseDouble(value.toString());
    }
}

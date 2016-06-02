package net.serenitybdd.screenplay.questions.converters;

public class FloatConverter implements Converter<Float> {
    @Override
    public Float convert(Object value) {
        return Float.parseFloat(value.toString());
    }
}

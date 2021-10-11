package net.serenitybdd.screenplay.questions.converters.converters;

public class FloatConverter implements Converter<Float> {
    @Override
    public Float convert(Object value) {
        return Float.parseFloat(value.toString());
    }
}

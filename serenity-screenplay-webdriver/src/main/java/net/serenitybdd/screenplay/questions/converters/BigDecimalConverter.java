package net.serenitybdd.screenplay.questions.converters;

import java.math.BigDecimal;

public class BigDecimalConverter implements Converter<BigDecimal> {
    @Override
    public BigDecimal convert(Object value) {
        return new BigDecimal(value.toString());
    }
}

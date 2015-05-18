package net.thucydides.core.csv.converters;

import java.math.BigDecimal;

public class BigDecimalTypeConverter implements TypeConverter {
    @Override
    public boolean appliesTo(Class<?> type) {
        return type.isAssignableFrom(BigDecimal.class);
    }

    @Override
    public Object valueOf(Object fieldValue) {
        return new BigDecimal(fieldValue.toString());
    }
}

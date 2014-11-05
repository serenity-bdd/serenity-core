package net.thucydides.core.csv.converters;

public class BooleanTypeConverter implements TypeConverter {
    @Override
    public boolean appliesTo(Class<?> type) {
        return type.isAssignableFrom(Boolean.class);
    }

    @Override
    public Object valueOf(Object fieldValue) {
        return Boolean.valueOf(fieldValue.toString());
    }
}

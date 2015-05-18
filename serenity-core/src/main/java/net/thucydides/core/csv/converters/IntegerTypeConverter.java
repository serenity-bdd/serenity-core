package net.thucydides.core.csv.converters;

public class IntegerTypeConverter implements TypeConverter {
    @Override
    public boolean appliesTo(Class<?> type) {
        return type.isAssignableFrom(Integer.class) || type.getName().equals("int");
    }

    @Override
    public Object valueOf(Object fieldValue) {
        return Integer.valueOf(fieldValue.toString());
    }
}

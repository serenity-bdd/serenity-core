package net.thucydides.core.csv.converters;

public interface TypeConverter {
    public abstract boolean appliesTo(Class<?> type);
    Object valueOf(Object fieldValue);
}

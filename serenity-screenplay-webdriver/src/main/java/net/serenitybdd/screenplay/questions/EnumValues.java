package net.serenitybdd.screenplay.questions;

import com.google.common.collect.Lists;
import net.serenitybdd.screenplay.exceptions.UnexpectedEnumValueException;
import net.thucydides.core.annotations.Methods;

import java.util.ArrayList;
import java.util.List;

public class EnumValues {

    private final Class<?> enumType;

    public <T> EnumValues(Class<T> enumType) {
        this.enumType = enumType;
    }

    protected <T> T convertToEnum(Class<T> enumType, String value) {
        try {
            return (T) Methods.of(enumType).called("valueOf").first().invoke(null, value);
        } catch (Exception e) {
            throw new UnexpectedEnumValueException("Unknown enum value for "+ enumType + " of " + value);
        }
    }

    public static EnumValues forType(Class<?> enumType) {
        return new EnumValues(enumType);
    }

    public <T> T getValueOf(String value) {
        return (T) convertToEnum(enumType, value);
    }

    public <T> List<T> getValuesOf(List<?> values) {
        List<T> convertedValues = new ArrayList<>();
        Object lastAttemptedValue = null;
        try {
            for(Object value : values) {
                lastAttemptedValue = value;
                convertedValues.add((T)Methods.of(enumType).called("valueOf").first().invoke(null, value.toString()));
            }
        } catch (Exception e) {
            throw new UnexpectedEnumValueException("Unknown enum value for "+ enumType + ": " + lastAttemptedValue);
        }
        return convertedValues;
    }
}

package net.serenitybdd.screenplay;

import net.thucydides.core.annotations.Methods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnumValues {

    private final Class<?> enumType;

    public <T> EnumValues(Class<T> enumType) {
        this.enumType = enumType;
    }

    protected <T> T convertToEnum(Class<T> enumType, String value) {
        try {
            return (T) Methods.of(enumType).called("valueOf").first().invoke(null, value);
        } catch (Exception e) {
            throw new UnexpectedEnumValueException("Unknown enum value for " + enumType + " of " + value);
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
        for (Object value : values) {
            convertedValues.addAll(
                    Methods.of(enumType).called("valueOf").asList().stream()
                            .filter(method -> method.getParameterCount() == 1)
                            .filter(method -> method.getParameterTypes()[0] == String.class)
                            .map(method -> (T) stringToValue(method, value))
                            .collect(Collectors.toList())
            );
        }
        return convertedValues;
    }

    private static <T> T stringToValue(Method method, Object value) {
        try {
            return (T) method.invoke(null, value.toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new UnexpectedEnumValueException("Unexpected enum value for " + method.getDeclaringClass().getSimpleName() + " of " + value);
        }
    }
}

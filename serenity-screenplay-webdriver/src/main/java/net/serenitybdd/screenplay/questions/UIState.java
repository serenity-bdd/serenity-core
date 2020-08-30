package net.serenitybdd.screenplay.questions;

import com.google.common.base.Preconditions;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.questions.converters.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UIState<T> {

    protected final Actor actor;

    protected Map<Class<?>, Converter> DEFAULT_CONVERTERS = new HashMap();
    {
        DEFAULT_CONVERTERS.put(String.class, new StringConverter());
        DEFAULT_CONVERTERS.put(Boolean.class, new BooleanConverter());
        DEFAULT_CONVERTERS.put(DateTime.class, new DateTimeConverter());
        DEFAULT_CONVERTERS.put(Float.class, new FloatConverter());
        DEFAULT_CONVERTERS.put(Double.class, new DoubleConverter());
        DEFAULT_CONVERTERS.put(Integer.class, new IntegerConverter());
        DEFAULT_CONVERTERS.put(Long.class, new LongConverter());
        DEFAULT_CONVERTERS.put(BigDecimal.class, new BigDecimalConverter());
    }

    protected UIState(Actor actor) {
        this.actor = actor;
    }

    public abstract T resolve();

    public T value() { return resolve(); }

    public <TARGET> TARGET as(Class<TARGET> type) {
        return (TARGET) converterFor(type).convert(resolve());
    }

    public String asString() {
        return as(String.class);
    }

    public Integer asInteger() {
        return as(Integer.class);
    }

    public Double asDouble() {
        return as(Double.class);
    }

    public Float asFloat() {
        return as(Float.class);
    }

    public Long asLong() {
        return as(Long.class);
    }

    public Boolean asBoolean() {
        return as(Boolean.class);
    }

    public DateTime asDate() {
        return as(DateTime.class);
    }

    public LocalDate asLocalDate() {
        return as(LocalDate.class);
    }

    public BigDecimal asBigDecimal() {
        return as(BigDecimal.class);
    }

    public LocalDate asLocalDate(String format) {
        return LocalDate.parse(format, DateTimeFormatter.ofPattern(format));
    }

    public DateTime asDate(String format) {
        return DateTime.parse(resolve().toString(), DateTimeFormat.forPattern(format));
    }

    public <T> T asEnum(Class<T> enumType) {
        String value = resolve().toString();
        return EnumValues.forType(enumType).getValueOf(value);
    }

    protected <T> List<T> convertToEnums(Class<T> enumType, List<?> values) {
        return EnumValues.forType(enumType).getValuesOf(values);
    }

    protected Converter converterFor(Class<?> type) {
        Preconditions.checkState(DEFAULT_CONVERTERS.containsKey(type),"No converter found for " + type);
        return DEFAULT_CONVERTERS.get(type);
    }
}

package net.serenitybdd.screenplay.questions;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.questions.converters.*;
import net.serenitybdd.screenplay.targets.Target;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;
import java.util.Map;

public abstract class UIState<T> {

    protected final Target target;
    protected final Actor actor;

    protected Map<Class<?>, Converter> DEFAULT_CONVERTERS = Maps.newHashMap();
    {
        DEFAULT_CONVERTERS.put(Boolean.class, new BooleanConverter());
        DEFAULT_CONVERTERS.put(DateTime.class, new DateTimeConverter());
        DEFAULT_CONVERTERS.put(Double.class, new DoubleConverter());
        DEFAULT_CONVERTERS.put(Float.class, new FloatConverter());
        DEFAULT_CONVERTERS.put(Integer.class, new IntegerConverter());
        DEFAULT_CONVERTERS.put(String.class, new StringConverter());
        DEFAULT_CONVERTERS.put(Long.class, new LongConverter());
    }

    protected UIState(Target target, Actor actor) {
        this.target = target;
        this.actor = actor;
    }

    public abstract T resolve();
    public abstract List<T> resolveAll();

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

    public Boolean asBoolean() {
        return as(Boolean.class);
    }

    public DateTime asDate() {
        return as(DateTime.class);
    }

    public DateTime asDate(String format) {
        return DateTime.parse(resolve().toString(), DateTimeFormat.forPattern(format));
    }

    public <T> T asEnum(Class<T> enumType) {
        String value = resolve().toString();
        return EnumValues.forType(enumType).getValueOf(value);
    }

    public List<T> asList() {
        return resolveAll();
    }

    public <T> List<T> asListOf(Class<T> type) {
        return convertToEnums(type, asList());
    }

    protected <T> List<T> convertToEnums(Class<T> enumType, List<?> values) {
        return EnumValues.forType(enumType).getValuesOf(values);
    }

    protected Converter converterFor(Class<?> type) {
        Preconditions.checkState(DEFAULT_CONVERTERS.containsKey(type),"No converter found for " + type);
        return DEFAULT_CONVERTERS.get(type);
    }
}

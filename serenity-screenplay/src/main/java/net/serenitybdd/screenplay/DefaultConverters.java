package net.serenitybdd.screenplay;

import com.google.common.base.Preconditions;
import net.serenitybdd.screenplay.questions.converters.converters.*;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DefaultConverters {
    protected static final Map<Class<?>, Converter<?>> DEFAULT_CONVERTERS = new HashMap<>();
    static {
        DEFAULT_CONVERTERS.put(String.class, new StringConverter());
        DEFAULT_CONVERTERS.put(Boolean.class, new BooleanConverter());
        DEFAULT_CONVERTERS.put(DateTime.class, new DateTimeConverter());
        DEFAULT_CONVERTERS.put(Float.class, new FloatConverter());
        DEFAULT_CONVERTERS.put(Double.class, new DoubleConverter());
        DEFAULT_CONVERTERS.put(Integer.class, new IntegerConverter());
        DEFAULT_CONVERTERS.put(Long.class, new LongConverter());
        DEFAULT_CONVERTERS.put(BigDecimal.class, new BigDecimalConverter());
    }

    public static Converter<?> converterFor(Class<?> type) {
        Preconditions.checkState(DEFAULT_CONVERTERS.containsKey(type),"No converter found for " + type);
        return DEFAULT_CONVERTERS.get(type);
    }

}

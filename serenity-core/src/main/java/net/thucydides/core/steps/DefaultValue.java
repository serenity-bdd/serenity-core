package net.thucydides.core.steps;

import org.joda.time.DateTime;

import java.lang.reflect.Method;
import java.util.*;

public class DefaultValue {

    private static final Map<Class<?>, Object> DEFAULT_VALUES = new HashMap<Class<?>, Object>();
    static {
        DEFAULT_VALUES.put(String.class, "");
        DEFAULT_VALUES.put(Integer.class, 0);
        DEFAULT_VALUES.put(Long.class, 0L);
        DEFAULT_VALUES.put(Boolean.class, false);
        DEFAULT_VALUES.put(Double.class, 0.0);
        DEFAULT_VALUES.put(Float.class, 0.0F);
        DEFAULT_VALUES.put(Date.class, new DateTime(2000,1,1,0,0,0).toDate());
        DEFAULT_VALUES.put(DateTime.class, new DateTime(2000,1,1,0,0,0));
        DEFAULT_VALUES.put(List.class, Collections.EMPTY_LIST);
        DEFAULT_VALUES.put(Set.class, Collections.EMPTY_SET);
        DEFAULT_VALUES.put(Map.class, Collections.EMPTY_MAP);
    }

    public static Object defaultReturnValueFor(Method method, Object object) {
        if (method.getReturnType().isAssignableFrom(object.getClass())) {
            return object;
        } else {
            return DefaultValue.forClass(method.getReturnType());
        }
    }

    public static Object forClass(Class<?> declaringClass) {
        Object defaultValue = null;

        Set<Class<?>> classes = DEFAULT_VALUES.keySet();
        for (Class<?> returnType : classes) {
            if (returnType.isAssignableFrom(declaringClass)) {
                defaultValue = DEFAULT_VALUES.get(returnType);
                break;
            }
        }
        return defaultValue;
    }
}

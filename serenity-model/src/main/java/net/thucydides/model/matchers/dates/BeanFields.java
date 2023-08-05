package net.thucydides.model.matchers.dates;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.Map;

public class BeanFields {

    private final Object bean;

    private BeanFields(Object bean) {
        this.bean = bean;
    }

    public static BeanFields fieldValueIn(Object bean) {
        return new BeanFields(bean);
    }
    
    public Object forField(String fieldName) {
        try {
            if (isAMap(bean)) {
               return  ((Map) bean).get(fieldName);
            } else {
                return PropertyUtils.getProperty(bean, fieldName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }
    }

    private boolean isAMap(Object bean) {
        return (Map.class.isAssignableFrom(bean.getClass()));
    }

}

package net.thucydides.model.matchers.dates;

import java.util.Map;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

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
//                return PropertyUtils.getProperty(bean, fieldName);
                return getProperty(bean, fieldName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not find property value for " + fieldName);
        }
    }

    public Object getProperty(Object bean, String propertyName) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (pd.getName().equals(propertyName)) {
                return pd.getReadMethod().invoke(bean);
            }
        }
        throw new IllegalArgumentException("Property not found: " + propertyName);
    }
    private boolean isAMap(Object bean) {
        return (Map.class.isAssignableFrom(bean.getClass()));
    }

}

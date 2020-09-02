package net.thucydides.core.matchers;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BeanMatcherAsserts {
    private static final String NEW_LINE = System.getProperty("line.separator");

    public static <T> boolean matches(List<T> elements, BeanMatcher... matchers) {
        List<T> filteredElements = filterElements(elements, matchers);
        return apply(filteredElements, collectionMatchersIn(matchers));
    }

    private static <T> boolean apply(final List<T> elements, final List<BeanCollectionMatcher> matchers) {
        List<BeanCollectionMatcher> collectionMatchers = addEmptyTestIfNoCountChecksArePresentTo(matchers);

        for (BeanCollectionMatcher matcher : collectionMatchers) {
            if (!matcher.matches(elements)) {
                return false;
            }
        }
        return true;
    }

    private static List<BeanCollectionMatcher> addEmptyTestIfNoCountChecksArePresentTo(final List<BeanCollectionMatcher> matchers) {
        if (thereIsACardinalityConstraintSpecifiedInThe(matchers)) {
            return matchers;
        } else {
            return mustContainAtLeastOneItemMatching(matchers);
        }
    }

    private static List mustContainAtLeastOneItemMatching(List<BeanCollectionMatcher> matchers) {
        return ListUtils.union(matchers, Arrays.asList(BeanMatchers.the_count(is(not(0)))));
    }

    private static boolean thereIsACardinalityConstraintSpecifiedInThe(List<BeanCollectionMatcher> matchers) {
        for(BeanCollectionMatcher matcher : matchers) {
            if (matcher instanceof BeanCountMatcher) {
                return true;
            }
        }
        return false;
    }

    private static List<BeanCollectionMatcher> collectionMatchersIn(final BeanMatcher[] matchers) {

        List<BeanMatcher> compatibleMatchers = Arrays.stream(matchers)
                .filter(matcher -> matcher instanceof BeanCollectionMatcher)
                .collect(Collectors.toList());

        return compatibleMatchers.stream()
                .map( matcher -> (BeanCollectionMatcher) matcher)
                .collect(Collectors.toList());
    }

    public static <T> List<T> filterElements(final List<T> elements, final BeanMatcher... matchers) {

        List<BeanFieldMatcher> propertyMatchers = propertyMatchersIn(matchers);

        return elements.stream()
                .filter( element -> elementMatches(element, propertyMatchers) )
                .collect(Collectors.toList());
    }

    private static <T> boolean elementMatches(T element, List<BeanFieldMatcher> propertyMatchers) {
        return propertyMatchers.stream().allMatch(
                propertyMatcher -> propertyMatcher.matches(element)
        );
    }

    private static List<BeanFieldMatcher> propertyMatchersIn(BeanMatcher[] matchers) {
        List<BeanMatcher> compatibleMatchers = Arrays.stream(matchers)
                .filter( matcher -> matcher instanceof BeanFieldMatcher)
                .collect(Collectors.toList());

        return compatibleMatchers.stream().map(matcher -> (BeanFieldMatcher) matcher).collect(Collectors.toList());
    }

    public static <T> void shouldMatch(List<T> items, BeanMatcher... matchers) {
        if (!matches(items, matchers)) {
            throw new AssertionError("Failed to find matching elements for " + StringUtils.join(matchers)
                                     + NEW_LINE
                                     +"Elements where " + StringUtils.join(items));
        }
    }

    public static <T> void shouldMatch(T bean, BeanMatcher... matchers) {
        if (!matches(bean, matchers)) {
            throw new AssertionError("Expected " + Arrays.toString(matchers) +
                                     " but was " + descriptionOf(bean));


        }
    }

    public static <T> void shouldNotMatch(List<T> items, BeanMatcher... matchers) {
        if (matches(items, matchers)) {
            throw new AssertionError("Found unneeded matching elements for " + join(matchers)
                    + NEW_LINE
                    +"Elements where " + join(items));
        }
    }

    private static String descriptionOf(Object bean) {

        if (isAMap(bean)) {
            return mapDescription((Map<String, ? extends Object>) bean);
        } else {
            return beanDescription(bean);
        }
    }

    private static String beanDescription(Object bean) {
        List<String> propertyTerms = new ArrayList<String>();
        try {
            for(PropertyDescriptor descriptor : propertiesOf(bean)) {
                Method getter = descriptor.getReadMethod();
                if (getter != null) {
                    propertyTerms.add(propertyValueOf(descriptor.getDisplayName(), Optional.ofNullable(getter.invoke(bean)).map(Object::toString).orElse("null")));
                }
            }
            return join(propertyTerms);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not read bean properties", e);
        }
    }

    private static String mapDescription(Map<String, ? extends Object> map) {
        List<String> propertyTerms = new ArrayList<String>();

        for (String key : map.keySet()) {
            propertyTerms.add(propertyValueOf(key, map.get(key).toString()));
        }
        return join(propertyTerms);
    }

    private static boolean isAMap(Object bean) {
        return Map.class.isAssignableFrom(bean.getClass());
    }

    public static <T> boolean matches(T bean, BeanMatcher... matchers) {
        return matches(Arrays.asList(bean), matchers);
    }

    private static String propertyValueOf(String propertyName, String value) {
        return propertyName + " = '" + value + "'";
    }

    private static <T> PropertyDescriptor[] propertiesOf(T bean) throws IntrospectionException {
        return Introspector.getBeanInfo(bean.getClass(), Object.class)
                .getPropertyDescriptors();
    }

}

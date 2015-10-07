package net.thucydides.core.annotations;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 7/08/2015.
 */
public class Methods {
    private static final Logger LOGGER = LoggerFactory.getLogger(Fields.class);

    private final Class<?> clazz;

    private List<Method> currentResults = Lists.newArrayList();

    public static Methods of(final Class<?> testClass) {
        return new Methods(testClass);
    }

    public Methods(Class<?> clazz) {
        this.clazz = clazz;
        this.currentResults = Arrays.asList(clazz.getMethods());
    }

    public Methods called(String name) {
        currentResults = filterByName(currentResults, name);
        return this;
    }

    private List<Method> filterByName(List<Method> currentResults, String name) {
        List<Method> filteredResults = Lists.newArrayList();
        for (Method method : currentResults) {
            if (method.getName().equals(name)) {
                filteredResults.add(method);
            }
        }
        return filteredResults;
    }

    public List<Method> asList() {
        return currentResults;
    }

    public boolean isEmpty() {
        return currentResults.isEmpty();
    }

    public Method first() {
        Preconditions.checkState(!currentResults.isEmpty());
        return currentResults.get(0);
    }
}

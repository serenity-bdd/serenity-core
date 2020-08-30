package net.thucydides.core.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Somewhere to hold the Guice injector.
 * There might be a better way to do this.
 */
public class Injectors {

    private static Map<String,Injector>  injectors = Collections.synchronizedMap(new HashMap<>());


    public static synchronized Injector getInjector() {
        return getInjector(new ThucydidesModule());
    }
    
    public static synchronized Injector getInjector(com.google.inject.Module module) {
        String moduleClassName = module.getClass().getCanonicalName();
        Injector injector = injectors.get(moduleClassName);
        if (injector == null) {
    		injector = Guice.createInjector(module);
    		injectors.put(moduleClassName, injector);
    	}
    	return injector;
    }
}
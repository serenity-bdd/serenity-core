package net.thucydides.core.guice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Somewhere to hold the Guice injector.
 * There might be a better way to do this.
 */
public class Injectors {
    private static final Logger LOGGER = LoggerFactory.getLogger(Injectors.class);

    private static final Map<String, Injector> injectors = Collections.synchronizedMap(new HashMap<>());

    private static Module defaultModule;

    public static synchronized void setDefaultModule(Module module) {
        defaultModule = module;
        LOGGER.trace("Set default guice module {}", module);
    }

    public static synchronized Injector getInjector() {
        if (defaultModule == null) {
            defaultModule = new ThucydidesModule();
        }
        return getInjector(defaultModule);
    }

    public static synchronized Injector getInjector(com.google.inject.Module module) {
        String moduleClassName = module.getClass().getCanonicalName();
        Injector injector = injectors.get(moduleClassName);
        if (injector == null) {
            injector = Guice.createInjector(module);
            injectors.put(moduleClassName, injector);
            LOGGER.debug("Created injector for module {}", moduleClassName);
        }
        return injector;
    }
}
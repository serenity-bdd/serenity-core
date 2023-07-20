package net.thucydides.core.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Injectors {
    private static final Logger LOGGER = LoggerFactory.getLogger(Injectors.class);

    private static final Map<String, Injector> injectors = new ConcurrentHashMap<>();

    private static volatile Module defaultModule;

    public static Injector getInjector() {
        if (defaultModule == null) {
            synchronized (Injectors.class) {
                if (defaultModule == null) {
                    defaultModule = new ThucydidesModule();
                }
            }
        }
        return getInjector(defaultModule);
    }

    public static Injector getInjector(Module module) {
        String moduleClassName = module.getClass().getCanonicalName();
        Injector injector = injectors.get(moduleClassName);
        if (injector == null) {
            synchronized (Injectors.class) {
                injector = injectors.get(moduleClassName);
                if (injector == null) {
                    injector = Guice.createInjector(module);
                    injectors.put(moduleClassName, injector);
                    LOGGER.debug("Created injector for module {}", moduleClassName);
                }
            }
        }
        return injector;
    }
}

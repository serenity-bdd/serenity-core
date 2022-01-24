package net.thucydides.core.model.screenshots;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.model.TakeScreenshots;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.Inflector;

import java.util.*;

public class ScreenshotPreferencesByClass {
    private static final String SERENITY_TAKE_SCREENSHOTS_FOR = "serenity.take.screenshots.for.";
    private final Class<?> declaringClass;
    private final Map<String, TakeScreenshots> classNameToScreenshotPreference;

    ScreenshotPreferencesByClass(Class<?> declaringClass, EnvironmentVariables environmentVariables) {
        this.declaringClass = declaringClass;
        this.classNameToScreenshotPreference = classNameToScreenshotPreferencesDefinedIn(environmentVariables);
    }

    private Map<String, TakeScreenshots> classNameToScreenshotPreferencesDefinedIn(EnvironmentVariables environmentVariables) {
        Map<String, TakeScreenshots> screenshotPreference = new HashMap<>();

        for (String key : environmentVariables.getKeys()) {
            if (key.startsWith(SERENITY_TAKE_SCREENSHOTS_FOR)) {

                String preference = EnvironmentSpecificConfiguration.from(environmentVariables)
                        .getOptionalProperty(key)
                        .orElse("FOR_EACH_ACTION");

                screenshotPreference.put(singularClassNameFrom(key).toLowerCase(),
                                         screenshotPreferenceValueFrom(preference));
                screenshotPreference.put(pluralClassNameFrom(key).toLowerCase(),
                                         screenshotPreferenceValueFrom(preference));
            }
        }
        return screenshotPreference;
    }

    private TakeScreenshots screenshotPreferenceValueFrom(String screenshotPreference) {
        try {
            return (TakeScreenshots.valueOf(screenshotPreference.toUpperCase()));
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Incorrectly configured screenshot value '" + screenshotPreference + "' for serenity.take.screenshots - should be one of "
                    + Arrays.toString(TakeScreenshots.values()));
        }
    }

    private String singularClassNameFrom(String key) {
        String simpleClassName = key.replace(SERENITY_TAKE_SCREENSHOTS_FOR,"");
        return Inflector.getInstance().singularize(simpleClassName);
    }

    private String pluralClassNameFrom(String key) {
        String simpleClassName = key.replace(SERENITY_TAKE_SCREENSHOTS_FOR,"");
        return Inflector.getInstance().pluralize(simpleClassName);
    }
    public static ScreenshotPreferencesByClassBuilder forClass(Class<?> declaringClass) {
        return new ScreenshotPreferencesByClassBuilder(declaringClass);
    }

    public Optional<TakeScreenshots> getScreenshotPreference() {
        List<Class<?>> candidateClasses = getSuperClassesAndInterfacesFrom(declaringClass);
        
        for(Class<?> candidateClass : candidateClasses) {
            String className = candidateClass.getSimpleName().toLowerCase();
            if (classNameToScreenshotPreference.containsKey(className)) {
                return Optional.of(classNameToScreenshotPreference.get(className));
            }
        }

        return Optional.empty();
    }

    private List<Class<?>> getSuperClassesAndInterfacesFrom(Class<?> declaringClass) {
        List<Class<?>> superClassesAndInterfaces = new ArrayList<>();
        
        superClassesAndInterfaces.add(declaringClass);

        Collection<Class<?>> superClasses = superclassesFrom(declaringClass);
        superClassesAndInterfaces.addAll(superclassesFrom(declaringClass));
        superClassesAndInterfaces.addAll(interfacesFrom(declaringClass));
        superClassesAndInterfaces.addAll(allInterfacesFrom(superClasses));

        return superClassesAndInterfaces;
    }

    private Collection<Class<?>> interfacesFrom(Class<?> declaringClass) {
        List<Class<?>> interfaces = new ArrayList<>();
        interfaces.addAll(Arrays.asList(declaringClass.getInterfaces()));
        for(Class<?> anInterface : declaringClass.getInterfaces()) {
            interfaces.addAll(superclassesFrom(anInterface));
            interfaces.addAll(interfacesFrom(anInterface));
        }
        return interfaces;
    }

    private Collection<Class<?>> allInterfacesFrom(Collection<Class<?>> superclasses) {
        List<Class<?>> interfaces = new ArrayList<>();
        for(Class<?> superclass : superclasses) {
            interfaces.addAll(interfacesFrom(superclass));
        }
        return interfaces;
    }

    private Collection<Class<?>> superclassesFrom(Class<?> declaringClass) {
        List<Class<?>> superClasses = new ArrayList<>();
        Class<?> superClass = declaringClass.getSuperclass();
        while (superClass != null) {
            superClasses.add(superClass);
            superClass = superClass.getSuperclass();
        }
        return superClasses;
    }


    public static class ScreenshotPreferencesByClassBuilder {
        private final Class<?> declaringClass;

        ScreenshotPreferencesByClassBuilder(Class<?> declaringClass) {
            this.declaringClass = declaringClass;
        }
        public ScreenshotPreferencesByClass withEnvironmentVariables(EnvironmentVariables environmentVariables) {
            return new ScreenshotPreferencesByClass(declaringClass, environmentVariables);
        }
    }
}

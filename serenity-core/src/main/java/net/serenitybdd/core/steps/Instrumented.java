package net.serenitybdd.core.steps;

import net.thucydides.core.steps.StepFactory;

public class Instrumented {

    private static StepFactory stepFactory = StepFactory.getFactory();

    public static <T> InstrumentedBuilder<T> instanceOf(Class<T> instanceClass) {
        return new InstrumentedBuilder<>(instanceClass);
    }


    public static class InstrumentedBuilder<T> {

        private final Class<T> instanceClass;

        private final Object[] constructorParameters;

        public InstrumentedBuilder(Class<T> instanceClass) {
            this(instanceClass,  new Object[]{});
        }

        public InstrumentedBuilder(Class<T> instanceClass, Object[] constructorParameters) {
            this.instanceClass = instanceClass;
            this.constructorParameters = constructorParameters;
        }

        public T newInstance() {
            return stepFactory.getUniqueStepLibraryFor(instanceClass, constructorParameters);
        }

        public T withProperties(Object... constructorParameters) {
            return new InstrumentedBuilder<T>(instanceClass, constructorParameters).newInstance();
        }

    }
}

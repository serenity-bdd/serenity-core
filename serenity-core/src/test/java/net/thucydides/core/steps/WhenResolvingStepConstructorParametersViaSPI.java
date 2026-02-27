package net.thucydides.core.steps;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.model.steps.StepParameterProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the StepParameterProvider SPI integration in StepFactory.
 * <p>
 * The SPI registration file at
 * META-INF/services/net.serenitybdd.model.steps.StepParameterProvider
 * registers {@link TestStringProvider} for these tests.
 */
class WhenResolvingStepConstructorParametersViaSPI {

    private StepFactory stepFactory;

    @BeforeEach
    void setUp() {
        stepFactory = new StepFactory();
    }

    // -- Test provider registered via SPI (see META-INF/services file) --

    /**
     * A test SPI provider that resolves CharSequence parameters with a marker value.
     * Uses CharSequence (not String) to avoid interfering with other tests that
     * use String constructor parameters with explicit values.
     */
    public static class TestCharSequenceProvider implements StepParameterProvider {
        public static final String RESOLVED_VALUE = "SPI-resolved-value";

        @Override
        public Optional<Object> resolve(Class<?> type) {
            if (type == CharSequence.class) {
                return Optional.of(RESOLVED_VALUE);
            }
            return Optional.empty();
        }
    }

    // -- Step library classes used in tests --

    public static class StepLibraryWithCharSequenceConstructor {
        private final CharSequence value;

        public StepLibraryWithCharSequenceConstructor(CharSequence value) {
            this.value = value;
        }

        public CharSequence getValue() {
            return value;
        }

        @Step
        public void doSomething() {}
    }

    public static class StepLibraryWithDefaultAndCharSequenceConstructor {
        private CharSequence value;

        public StepLibraryWithDefaultAndCharSequenceConstructor() {
            this.value = "default";
        }

        public StepLibraryWithDefaultAndCharSequenceConstructor(CharSequence value) {
            this.value = value;
        }

        public CharSequence getValue() {
            return value;
        }

        @Step
        public void doSomething() {}
    }

    public static class StepLibraryWithUnresolvableConstructor {
        private final Thread unresolvedParam;

        public StepLibraryWithUnresolvableConstructor(Thread unresolvedParam) {
            this.unresolvedParam = unresolvedParam;
        }

        @Step
        public void doSomething() {}
    }

    public static class StepLibraryWithUnresolvableAndDefaultConstructor {
        private String state = "default-constructed";

        public StepLibraryWithUnresolvableAndDefaultConstructor() {}

        public StepLibraryWithUnresolvableAndDefaultConstructor(Thread unresolvedParam) {
            this.state = "param-constructed";
        }

        public String getState() {
            return state;
        }

        @Step
        public void doSomething() {}
    }

    public static class SimpleStepLibrary {
        private String state = "default";

        public String getState() {
            return state;
        }

        @Step
        public void doSomething() {}
    }

    // -- Tests --

    @Nested
    class WhenSPIProviderCanResolveParameters {

        @Test
        void shouldUseResolvedParameterForStepLibraryWithOnlyParameterizedConstructor() {
            StepLibraryWithCharSequenceConstructor steps =
                    stepFactory.getNewStepLibraryFor(StepLibraryWithCharSequenceConstructor.class);

            assertThat(steps).isNotNull();
            assertThat(steps.getValue()).isEqualTo(TestCharSequenceProvider.RESOLVED_VALUE);
        }

        @Test
        void shouldUseResolvedParameterForStepLibraryWithBothConstructors() {
            StepLibraryWithDefaultAndCharSequenceConstructor steps =
                    stepFactory.getNewStepLibraryFor(StepLibraryWithDefaultAndCharSequenceConstructor.class);

            assertThat(steps).isNotNull();
            assertThat(steps.getValue()).isEqualTo(TestCharSequenceProvider.RESOLVED_VALUE);
        }

        @Test
        void shouldUseSPIForSharedStepLibraries() {
            StepLibraryWithCharSequenceConstructor steps =
                    stepFactory.getSharedStepLibraryFor(StepLibraryWithCharSequenceConstructor.class);

            assertThat(steps).isNotNull();
            assertThat(steps.getValue()).isEqualTo(TestCharSequenceProvider.RESOLVED_VALUE);
        }
    }

    @Nested
    class WhenExplicitParametersAreProvided {

        @Test
        void shouldPreferExplicitParametersOverSPI() {
            CharSequence explicitValue = "explicit-value";
            StepLibraryWithCharSequenceConstructor steps =
                    stepFactory.getUniqueStepLibraryFor(StepLibraryWithCharSequenceConstructor.class, explicitValue);

            assertThat(steps).isNotNull();
            assertThat(steps.getValue()).isEqualTo("explicit-value");
        }
    }

    @Nested
    class WhenSPIProviderCannotResolveParameters {

        @Test
        void shouldFallBackToDefaultConstructorWhenProviderCannotResolve() {
            StepLibraryWithUnresolvableAndDefaultConstructor steps =
                    stepFactory.getNewStepLibraryFor(StepLibraryWithUnresolvableAndDefaultConstructor.class);

            assertThat(steps).isNotNull();
            assertThat(steps.getState()).isEqualTo("default-constructed");
        }

        @Test
        void shouldCreateStepLibraryWithDefaultConstructorWhenNoParametersNeeded() {
            SimpleStepLibrary steps = stepFactory.getNewStepLibraryFor(SimpleStepLibrary.class);

            assertThat(steps).isNotNull();
            assertThat(steps.getState()).isEqualTo("default");
        }
    }
}

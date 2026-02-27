package net.serenitybdd.model.steps;

import java.util.Optional;

/**
 * SPI for providing constructor parameter values to step libraries created via {@code @Steps}.
 * <p>
 * Implementations are discovered via {@link java.util.ServiceLoader}. When Serenity creates
 * a step library that has a constructor with parameters but no explicit parameter values
 * were supplied, each registered provider is queried for each parameter type. If all
 * parameters can be resolved, the step library is constructed with those values.
 * </p>
 * <p>
 * This enables modules like serenity-playwright to supply constructor arguments
 * (e.g. a Playwright {@code Page}) without serenity-core having any compile-time
 * dependency on those modules.
 * </p>
 */
public interface StepParameterProvider {

    /**
     * Attempts to resolve a value for the given parameter type.
     *
     * @param type the declared type of the constructor parameter
     * @return an {@link Optional} containing the resolved value, or empty if this
     *         provider cannot supply the requested type
     */
    Optional<Object> resolve(Class<?> type);
}

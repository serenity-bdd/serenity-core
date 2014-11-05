package net.thucydides.core.scheduling;

import com.google.common.base.Function;

/**
 * Models a condition that might reasonably be expected to eventually evaluate to something that is
 * neither null nor false. Examples would include determining if a web page has loaded or that an
 * element is visible.
 * <p>
 * Note that it is expected that ExpectedConditions are idempotent. They will be called in a loop by
 * the {@link org.openqa.selenium.support.ui.WebDriverWait} and any modification of the state of the application under test may
 * have unexpected side-effects.
 *
 * @param <T> The return type
 */
public interface ExpectedBackendCondition<B,T> extends Function<B, T> {}
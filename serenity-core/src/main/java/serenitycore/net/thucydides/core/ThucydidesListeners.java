package serenitycore.net.thucydides.core;

import serenitycore.net.serenitybdd.core.SerenityListeners;
import serenitymodel.net.thucydides.core.webdriver.Configuration;

/** @deprecated Use SerenityListeners instead
 *
 */
@Deprecated
public class ThucydidesListeners extends SerenityListeners {
    public ThucydidesListeners(Configuration systemConfiguration) {
        super(systemConfiguration);
    }
}

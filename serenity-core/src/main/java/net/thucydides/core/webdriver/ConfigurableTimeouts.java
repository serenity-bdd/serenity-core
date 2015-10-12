package net.thucydides.core.webdriver;

import org.openqa.selenium.support.ui.Duration;

/**
 * Created by john on 12/03/15.
 */
public interface ConfigurableTimeouts {

    void setImplicitTimeout(Duration implicitTimeout);

    Duration getCurrentImplicitTimeout();

    public void resetTimeouts();
}

package net.thucydides.core.webdriver;

import java.time.Duration;

/**
 * Created by john on 12/03/15.
 */
public interface ConfigurableTimeouts {

    void setImplicitTimeout(Duration implicitTimeout);

    Duration getCurrentImplicitTimeout();

    public Duration resetTimeouts();
}

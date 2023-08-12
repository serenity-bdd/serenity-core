package net.thucydides.core.webdriver;

import net.thucydides.model.webdriver.Configuration;

public interface DriverConfiguration<T extends DriverConfiguration>  extends Configuration<T> {

    SupportedWebDriver getDriverType();
}

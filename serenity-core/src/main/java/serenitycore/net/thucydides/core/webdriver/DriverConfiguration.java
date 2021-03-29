package serenitycore.net.thucydides.core.webdriver;

import serenitymodel.net.thucydides.core.webdriver.Configuration;

public interface DriverConfiguration<T extends DriverConfiguration>  extends Configuration<T> {

    SupportedWebDriver getDriverType();
}

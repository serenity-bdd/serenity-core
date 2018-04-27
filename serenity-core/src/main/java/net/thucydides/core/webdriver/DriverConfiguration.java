package net.thucydides.core.webdriver;

public interface DriverConfiguration<T extends DriverConfiguration>  extends Configuration<T> {

    SupportedWebDriver getDriverType();
}

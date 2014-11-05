package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.WebDriver;

import java.net.URL;

public class NavigationStub implements WebDriver.Navigation {
    @Override
    public void back() {
    }

    @Override
    public void forward() {
    }

    @Override
    public void to(String url) {
    }

    @Override
    public void to(URL url) {
    }

    @Override
    public void refresh() {
    }
}

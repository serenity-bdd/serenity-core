package net.thucydides.core.webdriver.redimension;

import org.openqa.selenium.WebDriver;

class NoopRedimensioner implements Redimensioner {
    @Override
    public void redimension(WebDriver driver) {
    }
}

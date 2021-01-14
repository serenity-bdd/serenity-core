package net.thucydides.core.webdriver.redimension;

import org.openqa.selenium.WebDriver;

class FullScreenRedimensioner implements Redimensioner {

        @Override
        public void redimension(WebDriver driver) {
            driver.manage().window().fullscreen();
        }
    }
package net.thucydides.core.webdriver.redimension;

import org.openqa.selenium.WebDriver;

class MaximizeRedimensioner implements Redimensioner {

        @Override
        public void redimension(WebDriver driver) {
            driver.manage().window().maximize();
        }
    }
package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.remote.service.DriverService;

public class StopServiceHook extends Thread {

        private final DriverService service;

        public StopServiceHook(DriverService service) {
            this.service = service;
        }

        @Override
        public void run() {
            service.stop();
        }

    }
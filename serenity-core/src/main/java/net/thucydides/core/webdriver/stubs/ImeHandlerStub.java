package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.List;

public class ImeHandlerStub implements WebDriver.ImeHandler {
    @Override
    public List<String> getAvailableEngines() {
        return Collections.emptyList();
    }

    @Override
    public String getActiveEngine() {
        return "";  
    }

    @Override
    public boolean isActivated() {
        return false;  
    }

    @Override
    public void deactivate() {
        
    }

    @Override
    public void activateEngine(String engine) {
    }
}

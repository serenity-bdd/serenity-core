package net.thucydides.core.fluent;

import org.fluentlenium.core.FluentAdapter;
import org.openqa.selenium.WebDriver;

public class ThucydidesFluentAdapter extends FluentAdapter {
    public ThucydidesFluentAdapter(WebDriver driver) {
        initFluent(driver);
    }
}

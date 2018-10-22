package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebDriverInstanceEvents {

    private List<WebDriverInstanceEventListener> LISTENERS = Collections.synchronizedList(new ArrayList<>());

    private static WebDriverInstanceEvents eventsBus = new WebDriverInstanceEvents();

    public static WebDriverInstanceEvents bus() { return eventsBus; }

    public void register(WebDriverInstanceEventListener listener) {
        LISTENERS.add(listener);
    }

    public WebDriverInstanceEventBuilder notifyOf(WebDriverLifecycleEvent event) {
        return new WebDriverInstanceEventBuilder(event);
    }

    public class WebDriverInstanceEventBuilder {
        private final WebDriverLifecycleEvent event;

        WebDriverInstanceEventBuilder(WebDriverLifecycleEvent event) {
            this.event = event;
        }

        public void forDriver(WebDriver driver) {
            LISTENERS.forEach(
                    listener -> {
                        switch (event) {
                            case CLOSE:
                                listener.close(driver);
                                break;
                            case QUIT:
                                listener.quit(driver);
                                break;
                        }

                    }
            );
        }
    }
}

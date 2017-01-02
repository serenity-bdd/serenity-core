package net.thucydides.core.webdriver.redimension;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

class ResizeRedimensioner implements Redimensioner {
    private final int width;
    private final int height;


    public ResizeRedimensioner(EnvironmentVariables environmentVariables) {
        RedimensionConfiguration redimensionConfiguration = new RedimensionConfiguration(environmentVariables);
        this.width = redimensionConfiguration.getWidth();
        this.height = redimensionConfiguration.getHeight();
    }

    @Override
    public void redimension(WebDriver driver) {
        try {
            driver.manage().window().setSize(new Dimension(width, height));
            driver.manage().window().setPosition(new Point(0, 0));
        } catch (WebDriverException couldNotResizeScreen) {
            if (couldNotResizeScreen.getMessage().contains("Invalid requested size")) {
                driver.manage().window().maximize();
            }
        }
    }
}
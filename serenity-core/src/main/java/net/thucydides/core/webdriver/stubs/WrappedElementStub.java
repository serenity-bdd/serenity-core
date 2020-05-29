package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class WrappedElementStub implements WebElement {
    @Override
    public void click() {}

    @Override
    public void submit() {}

    @Override
    public void sendKeys(CharSequence... charSequences) {}

    @Override
    public void clear() {}

    @Override
    public String getTagName() {
        return "";
    }

    @Override
    public String getAttribute(String s) {
        return "";
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public <T extends WebElement> List<T> findElements(By by) {
        return new ArrayList<>();
    }

    @Override
    public <T extends WebElement> T findElement(By by) {
        return null;
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }

    @Override
    public Point getLocation() {
        return new Point(0,0);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(0,0);
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(0,0,0,0);
    }

    @Override
    public String getCssValue(String s) {
        return "";
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return null;
    }
}

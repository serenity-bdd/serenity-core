package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class WindowStub implements WebDriver.Window {
    @Override
    public void setSize(Dimension targetSize) {
    }


    @Override
    public void setPosition(Point targetPosition) {
    }

    @Override
    public Dimension getSize() {
        return new Dimension(0,0);
    }

    @Override
    public Point getPosition() {
        return new Point(0,0);
    }

    @Override
    public void maximize() {
    }

    @Override
    public void minimize() {

    }

    @Override
    public void fullscreen() {
    }
}

package net.serenitybdd.core.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SerenityActions extends Actions {
    public SerenityActions(WebDriver driver) {
        super(driver);
    }

    private WebElement wrappedElementIn(WebElement webElement) {
        if (webElement instanceof WebElementFacade) {
            return ((WebElementFacade) webElement).getElement();
        } else {
            return webElement;
        }
    }

    @Override
    public Actions keyDown(WebElement target, CharSequence key) {
        return super.keyDown(wrappedElementIn(target), key);
    }

    @Override
    public Actions keyUp(WebElement target, CharSequence key) {
        return super.keyUp(wrappedElementIn(target), key);
    }

    @Override
    public Actions sendKeys(WebElement target, CharSequence... keys) {
        return super.sendKeys(wrappedElementIn(target), keys);
    }

    @Override
    public Actions clickAndHold(WebElement target) {
        return super.clickAndHold(wrappedElementIn(target));
    }

    @Override
    public Actions release(WebElement target) {
        return super.release(wrappedElementIn(target));
    }

    @Override
    public Actions click(WebElement target) {
        return super.click(wrappedElementIn(target));
    }

    @Override
    public Actions doubleClick(WebElement target) {
        return super.doubleClick(wrappedElementIn(target));
    }

    @Override
    public Actions moveToElement(WebElement target) {
        return super.moveToElement(wrappedElementIn(target));
    }

    @Override
    public Actions moveToElement(WebElement target, int xOffset, int yOffset) {
        return super.moveToElement(wrappedElementIn(target), xOffset, yOffset);
    }

    @Override
    public Actions contextClick(WebElement target) {
        return super.contextClick(wrappedElementIn(target));
    }

    @Override
    public Actions dragAndDrop(WebElement source, WebElement target) {
        return super.dragAndDrop(source, wrappedElementIn(target));
    }

    @Override
    public Actions dragAndDropBy(WebElement source, int xOffset, int yOffset) {
        return super.dragAndDropBy(wrappedElementIn(source), xOffset, yOffset);
    }
}

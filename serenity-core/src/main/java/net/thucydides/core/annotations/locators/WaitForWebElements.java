package net.thucydides.core.annotations.locators;

import org.openqa.selenium.WebElement;

import java.util.List;

public interface WaitForWebElements {
    boolean areElementsReadyIn(List<WebElement> elements);
}

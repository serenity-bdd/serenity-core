package net.serenitybdd.core.selectors;

import org.openqa.selenium.By;

public interface SelectorConverter {
    By apply(String path);
}

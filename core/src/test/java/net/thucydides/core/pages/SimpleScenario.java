package net.thucydides.core.pages;

import net.serenitybdd.core.pages.Pages;
import net.thucydides.core.annotations.ManagedPages;

public class SimpleScenario {

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    public Pages getPages() {
        return pages;
    }
}

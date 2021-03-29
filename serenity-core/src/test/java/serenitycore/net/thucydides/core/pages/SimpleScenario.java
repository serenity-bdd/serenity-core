package serenitycore.net.thucydides.core.pages;

import serenitycore.net.thucydides.core.annotations.ManagedPages;
import serenitycore.net.thucydides.core.pages.Pages;

public class SimpleScenario {

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    public Pages getPages() {
        return pages;
    }
}

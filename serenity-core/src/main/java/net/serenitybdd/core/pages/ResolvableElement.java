package net.serenitybdd.core.pages;

import org.openqa.selenium.SearchContext;

public interface ResolvableElement {
    WebElementFacade resolveFor(PageObject pageObject);
    WebElementFacade resolveFor(SearchContext searchContext);
    ListOfWebElementFacades resolveAllFor(PageObject page);
    ListOfWebElementFacades resolveAllFor(SearchContext searchContext);
}
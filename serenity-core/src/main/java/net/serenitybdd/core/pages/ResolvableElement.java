package net.serenitybdd.core.pages;

public interface ResolvableElement {
    WebElementFacade resolveFor(PageObject pageObject);
    ListOfWebElementFacades resolveAllFor(PageObject page);
}
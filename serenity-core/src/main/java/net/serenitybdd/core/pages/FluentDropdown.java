package net.serenitybdd.core.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ISelect;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class FluentDropdown implements ISelect {
    private WebElementFacade webElementFacade;

    public FluentDropdown(WebElementFacade webElementFacade) {

        this.webElementFacade = webElementFacade;
    }

    protected Select select() {

        return new Select(webElementFacade.getElement());
    }

    @Override
    public boolean isMultiple() {
        return select().isMultiple();
    }

    @Override
    public List<WebElement> getOptions() {
        return select().getOptions();
    }

    @Override
    public List<WebElement> getAllSelectedOptions() {
        return select().getAllSelectedOptions();
    }

    public List<String> getAllSelectedValues() {
        return select().getAllSelectedOptions().stream().map(element -> element.getAttribute("value")).collect(Collectors.toList());
    }

    @Override
    public WebElement getFirstSelectedOption() {
        return select().getFirstSelectedOption();
    }

    @Override
    public void selectByVisibleText(String text) {
        select().selectByVisibleText(text);
    }

    public void byVisibleText(String text) {
        selectByVisibleText(text);
    }

    @Override
    public void selectByIndex(int index) {
        select().selectByIndex(index);
    }

    public void byIndex(int index) {
        selectByIndex(index);
    }

    @Override
    public void selectByValue(String value) {
        select().selectByValue(value);
    }

    public void byValue(String value) {
        selectByValue(value);
    }

    @Override
    public void deselectAll() {
        select().deselectAll();
    }

    @Override
    public void deselectByValue(String value) {
        select().deselectByValue(value);
    }

    @Override
    public void deselectByIndex(int index) {
        select().deselectByIndex(index);
    }

    @Override
    public void deselectByVisibleText(String text) {
        select().deselectByVisibleText(text);
    }
}

package net.thucydides.core.pages;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;

/**
 * A base class representing a WebDriver page object.
 *
 * @author johnsmart
 */
public abstract class PageObject extends net.serenitybdd.core.pages.PageObject {

    protected PageObject() {
        super();
    }

    protected PageObject(WebDriver driver, Predicate<? super net.serenitybdd.core.pages.PageObject> callback) {
        super(driver, callback);
    }

    public PageObject(WebDriver driver, int ajaxTimeout) {
        super(driver, ajaxTimeout);
    }

    public PageObject(WebDriver driver) {
        super(driver);
    }
}

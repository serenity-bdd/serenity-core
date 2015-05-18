package net.thucydides.core.webdriver.jquery;

public class ByJQuery {
    public static ByJQuerySelector selector(final String selectorExpression) {
        return new ByJQuerySelector(selectorExpression);
    }
}

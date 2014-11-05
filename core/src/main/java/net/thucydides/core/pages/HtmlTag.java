package net.thucydides.core.pages;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

public class HtmlTag {
    
    protected final WebElement webElement;

    private HtmlTag(WebElement webElement) {
        this.webElement = webElement;
    }

    public static HtmlTag from(WebElement webElement) {
        if (ButtonTag.canHandleThis(webElement)) {
            return new ButtonTag(webElement);
        }

        if (LinkTag.canHandleThis(webElement)) {
            return new LinkTag(webElement);
        }

        return new HtmlTag(webElement);
    }

    protected static StringMatcher tagFrom(WebElement webElement) {
        return new StringMatcher(webElement.getTagName());
    }
    
    protected static StringMatcher typeFrom(WebElement webElement) {
        return new StringMatcher(webElement.getAttribute("type"));
    }

    public String inHumanReadableForm() {
        String tagName = webElement.getTagName();
        String value = webElement.getAttribute("value");
        String text = webElement.getText();

        StringBuilder elementDescription = new StringBuilder(tagName);
        if (StringUtils.isNotEmpty(value)) {
            elementDescription.append(" - ").append(value);
        }
        if (StringUtils.isNotEmpty(text)) {
            elementDescription.append(" '").append(text).append("'");
        }
        return elementDescription.toString();
    }

    //
    // Implementation classes
    //
    static class ButtonTag extends HtmlTag {

        private ButtonTag(WebElement webElement) {
            super(webElement);
        }

        @Override
        public String inHumanReadableForm() {
            return "button: " + super.inHumanReadableForm();
        }

        public static boolean canHandleThis(WebElement webElement) {
            return (tagFrom(webElement).is("input")
                    && (typeFrom(webElement).is("button") || typeFrom(webElement).is("submit")));
        }
    }

    static class LinkTag extends HtmlTag {

        private LinkTag(WebElement webElement) {
            super(webElement);
        }

        @Override
        public String inHumanReadableForm() {
            return "link '" + webElement.getText() + "'";
        }

        public static boolean canHandleThis(WebElement webElement) {
            return tagFrom(webElement).is("a");
        }
    }
    
    static class StringMatcher {
        private final String value;

        StringMatcher(String value) {
            this.value = value;
        }

        public boolean is(String expectedValue) {
            return StringUtils.equals(value, expectedValue);
        }
    }
}

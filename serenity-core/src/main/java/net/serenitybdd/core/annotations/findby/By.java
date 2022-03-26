package net.serenitybdd.core.annotations.findby;

import com.google.common.base.Preconditions;
import net.thucydides.core.webdriver.shadow.ByShadow;
import org.openqa.selenium.*;

import java.util.List;

public abstract class By extends org.openqa.selenium.By {

    /**
     * @param scLocator The scLocator to use
     * @return a By which locates elements via AutoTest
     */
    public static By sclocator(final String scLocator) {
        Preconditions.checkNotNull(scLocator);
        return new ByScLocator(scLocator);
    }

    public static class ByScLocator extends By {
        private final String scLocator;

        public ByScLocator(String scLocator) {
            this.scLocator = scLocator;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            throw new IllegalArgumentException("SmartGWT does not provide the functionality to find multiple elements");
        }

        @Override
        public WebElement findElement(SearchContext context) {
            try {
            	WebElement element = (WebElement) ((JavascriptExecutor) context)
	                    .executeScript("return isc.AutoTest.getElement(arguments[0]);", scLocator);
            	if (element != null){
            		return element;
            	}
            } catch (WebDriverException e){
            	if ((Boolean) ((JavascriptExecutor) context)
            	.executeScript("return (typeof isc == 'undefined')")){
            		throw new NoSuchElementException("Not a SmartGWT page. Cannot locate element using SmartGTW locator " + toString());
            	}
            }
            throw new NoSuchElementException("Cannot locate element using " + toString());
        }

        @Override
        public String toString() {
            return "By.sclocator: " + scLocator;
        }
    }


    /**
     * @param jQuerySelector The jquery to use
     * @return a By selector object which locates elements via jQuery
     */
    public static By jquery(final String jQuerySelector) {
        Preconditions.checkNotNull(jQuerySelector);
        return new ByjQuerySelector(jQuerySelector);
    }

    public static By buttonText(final String text) {
        Preconditions.checkNotNull(text);
        return new ByButtonTextSelector(text);
    }

    public static class ByButtonTextSelector extends By {

        String buttonLabel;

        public ByButtonTextSelector(String buttonLabel) {
            this.buttonLabel = buttonLabel;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<WebElement> findElements(SearchContext context) {
            return context.findElements(byXpathforButtonWithLabel(buttonLabel));
        }

        @Override
        public WebElement findElement(SearchContext context) {

            WebElement element = context.findElement(byXpathforButtonWithLabel(buttonLabel));
            if (element != null){
                return element;
            }
            throw new NoSuchElementException("Cannot locate element using " + toString());
        }

        private org.openqa.selenium.By byXpathforButtonWithLabel(String buttonLabel) {
            return By.xpath(String.format("//*[normalize-space(.)=\"%s\"]", buttonLabel));
        }

        @Override
        public String toString() {
            return "By.buttonText: " + buttonLabel;
        }
    }


    public static class ByjQuerySelector extends By {
        private final String jQuerySelector;

        public ByjQuerySelector(String jQuerySelector) {
            this.jQuerySelector = jQuerySelector;
        }

        @SuppressWarnings("unchecked")
		@Override
        public List<WebElement> findElements(SearchContext context) {
        	List<WebElement> elements = (List<WebElement>) ((JavascriptExecutor) context)
                    .executeScript("var elements = $(arguments[0]).get(); return ((elements.length) ? elements : null)",
                    		jQuerySelector);
        	if (elements != null){
        		return elements;
        	}
            throw new NoSuchElementException("Cannot locate elements using " + toString());

        }

        @Override
        public WebElement findElement(SearchContext context) {
        	WebElement element = (WebElement) ((JavascriptExecutor) context)
                    .executeScript("return $(arguments[0]).get(0)", jQuerySelector);
        	if (element != null){
        		return element;
        	}
            throw new NoSuchElementException("Cannot locate element using " + toString());
        }

        @Override
        public String toString() {
            return "By.jQuerySelector: " + jQuerySelector;
        }
    }

    public static ByShadow.ByShadowBuilder shadowCSS(String cssLocator) {
        return ByShadow.css(cssLocator);
    }

    public static ByShadow.ByShadowBuilder shadowLocator(org.openqa.selenium.By shadowElementLocator) {
        return ByShadow.located(shadowElementLocator);
    }
}

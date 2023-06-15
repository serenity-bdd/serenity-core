package net.serenitybdd.core.pages;

import org.openqa.selenium.StaleElementReferenceException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListOfWebElementFacades extends ArrayList<WebElementFacade> {
    private Function<PageObject, ListOfWebElementFacades> fallback;
    private PageObject page;

    public ListOfWebElementFacades(Collection<? extends WebElementFacade> c) {
        super(c);
    }

    /**
     * Returns a list of the text values of each element in the collection
     */
    public List<String> texts() {
        return map(WebElementFacade::getText);
    }

    /**
     * Returns a list of the text contents of each element in the collection
     * This can be useful when elements are not visible on the page but are still in the DOM.
     */
    public List<String> textContents() {
        return map(WebElementFacade::getTextContent);
    }

    public <T> List<T> map(Function<? super WebElementFacade, T> elementConverter) {
        return convert((list) -> list.stream().map(elementConverter).collect(Collectors.toList()));
    }

    public List<? super WebElementFacade> filter(Predicate<? super WebElementFacade> condition) {
        return convert((list) -> list.stream().filter(condition).collect(Collectors.toList()));
    }

    public void setFallback(Function<PageObject, ListOfWebElementFacades> fallbackMethod, PageObject page) {
        this.fallback = fallbackMethod;
        this.page = page;
    }

    private <R> List<R> convert(Function<ListOfWebElementFacades, List<R>> transform) {
        try {
            return transform.apply(this);
        } catch (StaleElementReferenceException staleElementReferenceException) {
            if (fallback != null) {
                page.waitFor(1).second();
                return transform.apply(fallback.apply(page));
            } else {
                throw staleElementReferenceException;
            }
        }
    }
}

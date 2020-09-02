package net.serenitybdd.core.pages;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListOfWebElementFacades extends ArrayList<WebElementFacade> {
    public ListOfWebElementFacades(@NotNull Collection<? extends WebElementFacade> c) {
        super(c);
    }

    /**
     * Returns a list of the text values of each element in the collection
     */
    public List<String> texts() {
        return this.stream().map(WebElementFacade::getText).collect(Collectors.toList());
    }

    /**
     * Returns a list of the text contents of each element in the collection
     * This can be useful when elements are not visible on the page but are still in the DOM.
     */
    public List<String> textContents() {
        return this.stream().map(WebElementFacade::getTextContent).collect(Collectors.toList());
    }

    public <T> List<T> map(Function<? super WebElementFacade, T> elementConverter) {
        return this.stream().map(elementConverter).collect(Collectors.toList());
    }
}

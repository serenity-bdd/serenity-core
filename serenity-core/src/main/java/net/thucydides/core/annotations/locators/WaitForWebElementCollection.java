package net.thucydides.core.annotations.locators;

import net.thucydides.core.WebdriverCollectionStrategy;
import net.thucydides.core.annotations.ElementIsUsable;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.WebdriverCollectionStrategy.Optimistic;
import static net.thucydides.core.WebdriverCollectionStrategy.Paranoid;
import static net.thucydides.core.WebdriverCollectionStrategy.Pessimistic;

public class WaitForWebElementCollection {

    static Map<WebdriverCollectionStrategy,WaitForWebElements> COLLECTION_STRATEGY = new HashMap();
    static {
        COLLECTION_STRATEGY.put(Optimistic, new WaitForWebElements() {

            @Override
            public boolean areElementsReadyIn(List<WebElement> elements) {
                return ((elements != null));
            }
        });

        COLLECTION_STRATEGY.put(Pessimistic, new WaitForWebElements() {

            @Override
            public boolean areElementsReadyIn(List<WebElement> elements) {
                if (elements == null) {
                    return false;
                }
                return elements.isEmpty() || elements.get(0).isDisplayed();
            }
        });

        COLLECTION_STRATEGY.put(Paranoid, new WaitForWebElements() {

            @Override
            public boolean areElementsReadyIn(List<WebElement> elements) {
                if (elements == null) {
                    return false;
                }
                for (WebElement element : elements) {
                    if (!ElementIsUsable.forElement(element)) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    public static WaitForWebElements accordingTo(WebdriverCollectionStrategy collectionStrategy) {
        return COLLECTION_STRATEGY.get(collectionStrategy);
    }

}

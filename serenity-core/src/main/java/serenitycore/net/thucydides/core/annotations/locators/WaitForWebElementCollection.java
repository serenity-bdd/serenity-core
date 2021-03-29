package serenitycore.net.thucydides.core.annotations.locators;

import serenitycore.net.thucydides.core.WebdriverCollectionStrategy;
import serenitycore.net.thucydides.core.annotations.ElementIsUsable;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

import static serenitycore.net.thucydides.core.WebdriverCollectionStrategy.*;

public class WaitForWebElementCollection {

    static Map<WebdriverCollectionStrategy,WaitForWebElements> COLLECTION_STRATEGY = new HashMap();
    static {
        COLLECTION_STRATEGY.put(Optimistic, elements -> ((elements != null)));

        COLLECTION_STRATEGY.put(Pessimistic, elements -> {
            if (elements == null) {
                return false;
            }
            return elements.isEmpty() || ElementIsUsable.forElement(elements.get(0));
        });

        COLLECTION_STRATEGY.put(Paranoid, elements -> {
            if (elements == null) {
                return false;
            }
            for (WebElement element : elements) {
                if (!ElementIsUsable.forElement(element)) {
                    return false;
                }
            }
            return true;
        });
    }

    public static WaitForWebElements accordingTo(WebdriverCollectionStrategy collectionStrategy) {
        return COLLECTION_STRATEGY.get(collectionStrategy);
    }

}

package net.serenitybdd.screenplay.questions;

import com.beust.jcommander.internal.Lists;
import net.serenitybdd.core.pages.WebElementFacade;

import java.util.List;

/**
 * Created by john on 8/03/2016.
 */
public class UIFilter {

    public static List<WebElementFacade> visible(List<WebElementFacade> elements) {
        List<WebElementFacade> visibleElements = Lists.newArrayList();
        for(WebElementFacade element : elements) {
            if (element.isCurrentlyVisible()) {
                visibleElements.add(element);
            }
        }
        return elements;
    }

}

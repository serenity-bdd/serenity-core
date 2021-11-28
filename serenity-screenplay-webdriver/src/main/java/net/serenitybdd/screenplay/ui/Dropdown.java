package net.serenitybdd.screenplay.ui;

import net.serenitybdd.screenplay.targets.SearchableTarget;
import net.serenitybdd.screenplay.targets.Target;

import static net.serenitybdd.screenplay.ui.LocatorStrategies.fieldWithLabel;

public class Dropdown {

    private static final String BY_ID_OR_NAME = "css:select[id='{0}' i],select[name='{0}' i],select[data-test='{0}' i],select[class*='{0}']";
    private static final String BY_DEFAULT_OPTION = "//select[option[1][normalize-space(.)='{0}']]";

    public static SearchableTarget called(String name) {
        return Target.the("'" + name + "' dropdown").locatedBy(BY_ID_OR_NAME).of(name);
    }

    public static Target withLabel(String labelText) {
        return Target.the("the '" + labelText + "' dropdown").locatedBy(fieldWithLabel(labelText));
    }

    public static SearchableTarget withDefaultOption(String defaultOption) {
        return Target.the("the '" + defaultOption + " dropdown").locatedBy(BY_DEFAULT_OPTION).of(defaultOption);
    }

}

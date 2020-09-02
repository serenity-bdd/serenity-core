package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.screenplay.Interaction;
import org.openqa.selenium.Keys;

import java.util.ArrayList;
import java.util.List;

import static net.serenitybdd.screenplay.actions.type.RenderEnteredText.getFollowedByKeysDescriptionFor;
import static net.serenitybdd.screenplay.actions.type.RenderEnteredText.getTextAsStringFor;

public abstract class TypeValue implements Interaction {

    protected final CharSequence[] theText;
    protected final List<Keys> followedByKeys;
    protected String theTextAsAString;

    public TypeValue(CharSequence... theText) {
        this.theText = theText;
        this.followedByKeys = new ArrayList<>();
        this.theTextAsAString = getTextAsStringFor(theText);
    }

    public TypeValue thenHit(Keys... keys) {
        this.followedByKeys.addAll(NewList.of(keys));
        theTextAsAString = getTextAsStringFor(theText) + getFollowedByKeysDescriptionFor(followedByKeys);
        return this;
    }

    public Keys[] getFollowedByKeys() {
        return followedByKeys.toArray(new Keys[]{});
    }

}

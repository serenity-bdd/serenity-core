package net.serenitybdd.screenplay.actions;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.screenplay.Interaction;
import org.openqa.selenium.Keys;

import java.util.ArrayList;
import java.util.List;

import static net.serenitybdd.screenplay.actions.type.RenderEnteredText.getFollowedByKeysDescriptionFor;
import static net.serenitybdd.screenplay.actions.type.RenderEnteredText.getTextAsStringFor;

public abstract class EnterValue implements Interaction {

    protected final CharSequence[] theText;
    protected final List<Keys> followedByKeys;
    protected String theTextAsAString;

    public EnterValue(CharSequence... theText) {
        this.theText = theText;
        this.theTextAsAString = getTextAsStringFor(theText);
        this.followedByKeys = new ArrayList<>();
    }

    public EnterValue thenHit(Keys... keys) {
        this.followedByKeys.addAll(NewList.of(keys));
        theTextAsAString = getTextAsStringFor(theText) + getFollowedByKeysDescriptionFor(followedByKeys);
        return this;
    }

    public Keys[] getFollowedByKeys() {
        return followedByKeys.toArray(new Keys[]{});
    }


}

package net.serenitybdd.screenplay.actions;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.screenplay.Performable;
import org.openqa.selenium.Keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.serenitybdd.screenplay.actions.type.RenderEnteredText.getFollowedByKeysDescriptionFor;
import static net.serenitybdd.screenplay.actions.type.RenderEnteredText.getTextAsStringFor;

public abstract class EnterValue implements Performable {

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


    protected Optional<CharSequence[]> textValue() {
        if (theText == null) {
            return Optional.empty();
        }
        if (theText.length == 0) {
            return Optional.empty();
        }
        if (theText[0] == null) {
            return Optional.empty();
        }
        return Optional.of(theText);
    }
}

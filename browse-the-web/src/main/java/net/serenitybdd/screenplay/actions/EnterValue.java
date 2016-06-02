package net.serenitybdd.screenplay.actions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.serenitybdd.screenplay.Action;
import org.openqa.selenium.Keys;

import java.util.List;

public abstract class EnterValue implements Action {

    protected final String theText;
    protected final List<Keys> followedByKeys;
    private static final String ENTER_KEYS_INTRO_TEXT = " then hits ";

    public EnterValue(String theText) {
        this.theText = theText;
        this.followedByKeys = Lists.newArrayList();
    }

    public EnterValue thenHit(Keys... keys) {
        this.followedByKeys.addAll(Lists.newArrayList(keys));
        return this;
    }

    public Keys[] getFollowedByKeys() {
        return followedByKeys.toArray(new Keys[]{});
    }

    private String getFollowedByKeysDescriptionFor(List<Keys> keys) {
        if (keys.isEmpty()) {
            return "";
        }
        if (keys.size() == 1) {
            return ENTER_KEYS_INTRO_TEXT + KeyNames.of(keys);
        }
        if (keys.size() == 2) {
            return ENTER_KEYS_INTRO_TEXT + Joiner.on(" and ").join(KeyNames.of(keys));
        }

        String allButLastTwo = Joiner.on(", ").join(KeyNames.allButLastTwo(keys));
        String lastTwoKeys = Joiner.on(" and ").join(KeyNames.lastTwoOf(keys));
        String allKeys = Joiner.on(", ").join(ImmutableList.of(allButLastTwo, lastTwoKeys));

        return ENTER_KEYS_INTRO_TEXT + allKeys;
    }

}

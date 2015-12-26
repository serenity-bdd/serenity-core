package net.serenitybdd.screenplay.actions;

import com.google.common.collect.Lists;
import org.openqa.selenium.Keys;

import java.util.Arrays;
import java.util.List;

public abstract class EnterValue extends WebAction {

    protected final static List<Keys> NO_FOLLOWUP_KEYS = Lists.newArrayList();

    protected final String theText;
    protected List<Keys> followedByKeys = NO_FOLLOWUP_KEYS;

    public EnterValue(String theText) {
        this.theText = theText;
    }

    public EnterValue thenHit(Keys... keys) {
        this.followedByKeys = Arrays.asList(keys);
        return this;
    }

    public Keys[] getFollowedByKeys() {
        return followedByKeys.toArray(new Keys[]{});
    }

}

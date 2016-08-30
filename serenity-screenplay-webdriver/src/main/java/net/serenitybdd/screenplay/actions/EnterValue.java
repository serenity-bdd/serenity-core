package net.serenitybdd.screenplay.actions;

import com.google.common.collect.Lists;
import net.serenitybdd.screenplay.Interaction;
import org.openqa.selenium.Keys;

import java.util.List;

public abstract class EnterValue implements Interaction {

    protected final String theText;
    protected final List<Keys> followedByKeys;

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


}

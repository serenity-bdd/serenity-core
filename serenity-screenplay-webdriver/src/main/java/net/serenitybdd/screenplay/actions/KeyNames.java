package net.serenitybdd.screenplay.actions;

import com.google.common.collect.Lists;
import org.openqa.selenium.Keys;

import java.util.List;

public class KeyNames {
    public static List<String> of(List<Keys> keys) {
        List<String> pressedKeys = Lists.newArrayList();
        for(Keys key : keys) {
            pressedKeys.add(key.name());
        }
        return pressedKeys;
    }

    public static List<String> lastTwoOf(List<Keys> keys) {
        List<String> pressedKeys = of(keys);
        return pressedKeys.subList(pressedKeys.size() - 2, pressedKeys.size());
    }

    public static List<String> allButLastTwo(List<Keys> keys) {
        List<String> pressedKeys = of(keys);
        return pressedKeys.subList(0, pressedKeys.size() - 2);
    }

}

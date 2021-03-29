package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import org.openqa.selenium.Keys;

import java.util.List;
import java.util.stream.Collectors;

public class KeyNames {
    public static List<String> of(List<Keys> keys) {
        return keys.stream().map(Enum::name).collect(Collectors.toList());
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

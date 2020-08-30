package net.serenitybdd.screenplay.actions.type;

import net.serenitybdd.core.collect.NewList;
import net.serenitybdd.core.strings.Joiner;
import net.serenitybdd.screenplay.actions.KeyNames;
import org.openqa.selenium.Keys;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class RenderEnteredText {

    private static final String ENTER_KEYS_INTRO_TEXT = " then hits ";

    public static String getFollowedByKeysDescriptionFor(List<Keys> keys) {
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
        String allKeys = Joiner.on(", ").join(NewList.of(allButLastTwo, lastTwoKeys));

        return ENTER_KEYS_INTRO_TEXT + allKeys;
    }


    public static String getTextAsStringFor(CharSequence... theText) {

        return stream(theText).map(
                textValue -> (textValue instanceof Keys) ? ((Keys) textValue).name() : "'" + textValue.toString() + "'"
        ).collect(Collectors.joining(","));
    }

}

package net.thucydides.core.webdriver;

import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 14/07/2017.
 */
public class OptionsMap {

    public static Map<String, String> from(String options) {
        List<String> optionValues = Splitter.on(";").splitToList(options);
        Map<String, String> optionsMap = new HashMap<>();
        for (String optionValue : optionValues) {
            if (optionValue.contains("=")) {
                int equalsSign = optionValue.indexOf("=");
                String optionKey = optionValue.substring(0, equalsSign);
                String optionKeyValue = optionValue.substring(equalsSign + 1);
                optionsMap.put(optionKey, optionKeyValue);
            }
        }
        return optionsMap;
    }

}

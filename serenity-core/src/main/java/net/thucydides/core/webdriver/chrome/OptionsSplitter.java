package net.thucydides.core.webdriver.chrome;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

public class OptionsSplitter {
    public List<String> split(String chromeSwitches) {
        CharMatcher trimmable = CharMatcher.anyOf(" ,;");
        List<String> options = Splitter.on("--").omitEmptyStrings().trimResults(trimmable).splitToList(chromeSwitches);

        return options.stream()
                .map(option -> "--" + option)
                .distinct().collect(Collectors.toList());
    }
}

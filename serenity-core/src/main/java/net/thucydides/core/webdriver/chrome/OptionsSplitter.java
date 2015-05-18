package net.thucydides.core.webdriver.chrome;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * A description goes here.
 * User: john
 * Date: 7/03/2014
 * Time: 8:55 AM
 */
public class OptionsSplitter {
    public List<String> split(String chromeSwitches) {
        CharMatcher trimmable = CharMatcher.anyOf(" ,;");
        Splitter.on("--").trimResults().split(chromeSwitches);
        List<String> options = Lists.newArrayList(Splitter.on("--").omitEmptyStrings().trimResults(trimmable).split(chromeSwitches));
        return convert(options, withPrefixes());
    }

    private Converter<String, String> withPrefixes() {
        return new Converter<String, String>() {
            public String convert(String option) {
                return "--" + option;

            }
        };
    }

}

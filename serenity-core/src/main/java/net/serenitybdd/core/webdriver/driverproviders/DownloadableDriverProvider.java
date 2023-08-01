package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;

import java.util.List;

public abstract class DownloadableDriverProvider implements DriverProvider {

    protected List<String> argumentsIn(String options) {
        return Splitter.on(";").omitEmptyStrings().splitToList(options);
    }
}


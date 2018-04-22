package net.serenitybdd.screenplay.targets;

import org.openqa.selenium.By;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

public class IFrame {

    public final List<By> locators;

    private IFrame(By... locators) {
        this.locators = asList(locators);
    }

    public static IFrame withPath(By... locators) {
        return new IFrame(locators);
    }

    @Override
    public String toString() {
        return locators.stream().map(locator -> locator.toString().split(":")[1].trim()).collect(joining(", "));
    }

}

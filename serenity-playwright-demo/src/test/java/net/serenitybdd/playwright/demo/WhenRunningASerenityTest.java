package net.serenitybdd.playwright.demo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.playwright.PlaywrightSerenity;
import net.serenitybdd.playwright.demo.steps.NavigationSteps;
import net.serenitybdd.playwright.demo.steps.SearchSteps;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
class WhenRunningASerenityTest {

    @Test
    @DisplayName("A simple Serenity test")
    void shouldWorkFine() {
    }
}

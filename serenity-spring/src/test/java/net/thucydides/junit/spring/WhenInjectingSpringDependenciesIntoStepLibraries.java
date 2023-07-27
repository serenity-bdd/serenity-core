package net.thucydides.junit.spring;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SerenityRunner.class)
public class WhenInjectingSpringDependenciesIntoStepLibraries {

    @Managed
    WebDriver driver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;

    @Steps
    SpringEnabledStepLibrary springEnabledStepLibrary;

    @Test
    public void shouldInstanciateGizmoServiceInStepLibraries() {
        assertThat(springEnabledStepLibrary.gizmoService, is(not(nullValue())));
    }

    @Test
    public void shouldInstanciateNestedServicesInStepLibraries() {
        assertThat(springEnabledStepLibrary.gizmoService.getWidgetService(), is(not(nullValue())));
    }

    @Test
    public void shouldInstanciateServicesUsingTheResourceannotationInStepLibraries() {
        assertThat(springEnabledStepLibrary.gizmoDao, is(not(nullValue())));
    }

    @Test
    public void shouldAllowQualifiersInStepLibraries() {
        assertThat(springEnabledStepLibrary.premiumBazingaService.getName(), is("Premium Bazingas"));
    }

}

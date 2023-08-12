package net.thucydides.junit.integration.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.TestsRequirement;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.WrongPageError;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.junit.integration.pages.ApacheHomePage;
import net.thucydides.junit.integration.pages.ApacheProjectPage;

public class ApacheScenarioSteps extends ScenarioSteps {
    
    public ApacheScenarioSteps(Pages pages) {
        super(pages);
    }

    @Step
    @TestsRequirement("R123-1")
    public void clickOnProjects() throws WrongPageError {
        ApacheHomePage page = (ApacheHomePage) getPages().currentPageAt(ApacheHomePage.class);
        page.clickOnProjects();
    }
    
    @Step
    @TestsRequirement("R123-2") 
    public void clickOnCategories() throws WrongPageError {
        ApacheProjectPage page = (ApacheProjectPage) getPages().currentPageAt(ApacheProjectPage.class);
        page.clickOnCategories();
    }

    @Step
    public void clickOnInexistantLink() throws WrongPageError {
        ApacheProjectPage page = (ApacheProjectPage) getPages().currentPageAt(ApacheProjectPage.class);
        page.clickOnCategories();
    }

    @Step
    public void clickOnProjectAndCheckTitle() throws WrongPageError {
        ApacheProjectPage page = (ApacheProjectPage) getPages().currentPageAt(ApacheProjectPage.class);
        page.clickOnProjectsAndCheckTitle();
    }

    @Step @Pending
    public void notImplementedYet() throws WrongPageError {}
}

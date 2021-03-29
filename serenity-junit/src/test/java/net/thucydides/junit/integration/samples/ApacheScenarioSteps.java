package net.thucydides.junit.integration.samples;

import serenitymodel.net.thucydides.core.annotations.Pending;
import serenitymodel.net.thucydides.core.annotations.Step;
import serenitymodel.net.thucydides.core.annotations.TestsRequirement;
import serenitycore.net.thucydides.core.pages.Pages;
import serenitycore.net.thucydides.core.pages.WrongPageError;
import serenitycore.net.thucydides.core.steps.ScenarioSteps;
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

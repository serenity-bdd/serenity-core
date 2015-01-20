package net.thucydides.demo.steps;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import net.thucydides.demo.pages.GoogleHomePage;
import net.thucydides.demo.pages.GoogleResultsPage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

public class GoogleSearchSteps extends ScenarioSteps {
    
    public GoogleSearchSteps(Pages pages) {
        super(pages);
    }

    @Step
    public void open_home_page() {
        getPages().currentPageAt(GoogleHomePage.class);
    }

    @Step
    public void searchFor(String term) {
        GoogleHomePage page = (GoogleHomePage) getPages().currentPageAt(GoogleHomePage.class);
        page.searchFor(term);
    }
    
    @Step
    public void resultListShouldContain(String term) {
        GoogleResultsPage page = (GoogleResultsPage) getPages().currentPageAt(GoogleResultsPage.class);
        List<String> resultHeadings = page.getResultTitles();
        assertThat(resultHeadings, hasItem(containsString(term)));
    }
    
    @StepGroup("Open Google and search for term")
    public void open_google_and_search_for(String term) {
        open_home_page();
        searchFor(term);
    }
}

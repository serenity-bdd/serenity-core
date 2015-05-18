package net.thucydides.core.pages.integration;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { CheckingFieldContentWithTheFluentElementAPI.class,
                       CheckingVisibilityWithTheFluentElementAPI.class,
                       UsingFormsWithTheFluentElementAPI.class,
                       UsingTheWebElementFacade.class,
                       WaitingForElementsWithTheFluentElementAPI.class,
                       ReadingTableData.class})
public class  WhenUsingTheFluentElementAPI {
}

package net.thucydides.core.pages;

import com.google.common.base.Predicate;
import net.serenitybdd.core.pages.PageObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Dmytro Makhno
 */
public class WhenUsingCustomLogicForElementsInit {
    @Mock
    WebDriver driver;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    final class PageObjectWithCustomLogic extends PageObject{
        /**
         * Shows that custom logic was used
         */
        public boolean pagesPopulated; //don't put false here, callback is applied _before_ this is defined.

        public PageObjectWithCustomLogic(final WebDriver driver) {
            super(driver,new Predicate<PageObject>() {
                @Override
                public boolean apply(PageObject page) {
                    PageObjectWithCustomLogic customPage = (PageObjectWithCustomLogic)page;
                    assertFalse("Page was populated before actual population", customPage.pagesPopulated);
                    customPage.pagesPopulated = true; //in real HtmlElementLoader.populatePageObject(page, driver);
                    return true;
                }

                public boolean test(PageObject input) {
                    return false;
                }
            });
        }
    }

    @Test
    public void testTheCallOfCustomLogicDuringConstruction() {
        PageObjectWithCustomLogic pageObject = new PageObjectWithCustomLogic(driver);
        assertTrue("Page was not populated", pageObject.pagesPopulated);
    }
}

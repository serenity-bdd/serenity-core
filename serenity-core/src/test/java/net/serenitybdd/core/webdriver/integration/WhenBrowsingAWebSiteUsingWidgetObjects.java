package net.serenitybdd.core.webdriver.integration;

import net.serenitybdd.core.annotations.ImplementedBy;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WidgetObject;
import net.serenitybdd.core.pages.WidgetObjectImpl;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.configuration.SystemPropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public class WhenBrowsingAWebSiteUsingWidgetObjects {
	
	// expected numbers of rows and cards
	private static final int MAIN0_NUM_CARDS = 2;
	private static final int MAIN1_NUM_CARDS = 1;
	private static final int MAIN2_NUM_CARDS = 4;
	private static final int TOP_NUM_CARDS = 3;
	private static final int ORPHAN_CARDS = 1;
	private static final int TOTAL_CARDS = 
			TOP_NUM_CARDS + MAIN0_NUM_CARDS + MAIN1_NUM_CARDS + MAIN2_NUM_CARDS + ORPHAN_CARDS;

	// expected number of cards per MainRow
	private static final List<Integer> MAIN_NUM_CARDS = Arrays.asList(
			MAIN0_NUM_CARDS, 
			MAIN1_NUM_CARDS, 
			MAIN2_NUM_CARDS);
	
	// expected string values
	private static final String TOP_ROW = "TopRow";
	private static final String MAIN_ROW_FMT1 = "MainRow %d";
	private static final String ORPHAN_ROW = "Orphan";
	private static final String TITLE_FMT2 = "%s Card %d";
	private static final String CONTENT_FMT2 = "%s Card %d content";
	private static final String ORPHAN_TITLE = "Orphan Title";
	private static final String ORPHAN_CONTENT = "Orphan Content";

    public static class IndexPage extends PageObject {

    	@FindBy(css = "#topRow")
    	private CardContainer topRow;
    	
    	@FindBy(css = "#mainContent > ol > li")
    	private List<CardContainer> mainRows;
    	
    	@org.openqa.selenium.support.FindBy(css = ".card")
    	private List<Card> allCards;
    	
    	@FindBy(css = "#orphans > .card")
    	private Card orphanCard;
    	
    	@org.openqa.selenium.support.FindBy(css = "#orphans > .cardTitle")
    	private WebElement orphanTitle;

    	@FindBy(css = "#orphans > .cardContent")
    	private WebElement orphanContent;
    	
        public IndexPage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }
    
    @ImplementedBy(CardContainerImpl.class)
    public static interface CardContainer extends WidgetObject {
    	public List<Card> getCards();
    }

    @ImplementedBy(CardImpl.class)
    public static interface Card extends WidgetObject {
    	public String getTitle();
    	public String getContent();
    }
    
    public static class CardContainerImpl extends WidgetObjectImpl implements CardContainer {
    	
    	@FindBy(css = ".card")
    	private List<Card> cards;

		public CardContainerImpl(PageObject page, ElementLocator locator, long timeoutInMilliseconds) {
			super(page, locator, timeoutInMilliseconds);
		}

		public CardContainerImpl(PageObject page, ElementLocator locator, WebElement webElement, long timeoutInMilliseconds) {
			super(page, locator, webElement, timeoutInMilliseconds);
		}

		@Override
		public List<Card> getCards() {
			return cards;
		}
    }
    
    public static class CardImpl extends WidgetObjectImpl implements Card {
    	
    	@FindBy(css = ".cardTitle")
    	private WebElement title;
    	
    	@org.openqa.selenium.support.FindBy(css = ".cardContent")
    	private WebElementFacade content;

		public CardImpl(PageObject page, ElementLocator locator, long timeoutInMilliseconds) {
			super(page, locator, timeoutInMilliseconds);
		}

		public CardImpl(PageObject page, ElementLocator locator, WebElement webElement, long timeoutInMilliseconds) {
			super(page, locator, webElement, timeoutInMilliseconds);
		}

		@Override
		public String getTitle() {
			return title.getText();
		}

		@Override
		public String getContent() {
			return content.getText();
		}
    }
    
    WebDriver driver;

    IndexPage indexPage;

    MockEnvironmentVariables environmentVariables;

    Configuration configuration;

    @Before
    public void openLocalStaticSite() {
        driver = new HtmlUnitDriver();
        openStaticTestSite();
        indexPage = new IndexPage(driver, 1);
        indexPage.setWaitForTimeout(100);
    }

    @Before
    public void initConfiguration() {
        environmentVariables = new MockEnvironmentVariables();
        configuration = new SystemPropertiesConfiguration(environmentVariables);
    }

	@After
	public void closeDriver() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

    private void openStaticTestSite() {
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir, "src/test/resources/static-site/index-widget-objects.html");
        this.driver.get("file://" + testSite.getAbsolutePath());
    }

    @Test
    public void should_find_orphan_elements() {
    	assertThat("Unexpected orphan element title", indexPage.orphanTitle.getText(), is(ORPHAN_TITLE));
    	assertThat("Unexpected orphan element content", indexPage.orphanContent.getText(), is(ORPHAN_CONTENT));
    }
    
    @Test
    public void should_find_correct_number_of_cards() {
    	assertThat(indexPage.allCards.size(), is(TOTAL_CARDS));
    }
    
    @Test
    public void should_find_correct_numbers_of_widgets() {
    	// check the top row
    	assertNotNull(indexPage.topRow);
    	assertNotNull(indexPage.topRow.getCards());
    	assertThat(indexPage.topRow.getCards().size(), is(TOP_NUM_CARDS));
    	
    	// check the main rows
    	assertNotNull(indexPage.mainRows);
    	assertThat(indexPage.mainRows.size(), is(MAIN_NUM_CARDS.size()));
    	for (int i = 0; i < indexPage.mainRows.size(); i++) {
    		CardContainer mainRow = indexPage.mainRows.get(i);
    		assertNotNull(mainRow);
    		assertNotNull(mainRow.getCards());
    		assertThat(mainRow.getCards(), is(not(empty())));
    		assertThat(mainRow.getCards().size(), is(MAIN_NUM_CARDS.get(i)));
    	}
    }
    
    @Test
    public void should_find_top_row_of_cards() {
    	assertThatTheCorrectCardsAreFound(TOP_ROW, indexPage.topRow.getCards());
    }
    
    @Test
    public void should_find_main_rows_of_cards() {
    	assertNotNull(indexPage.mainRows);
    	assertThat(indexPage.mainRows, is(not(empty())));
    	for (int i = 0; i < indexPage.mainRows.size(); i++) {
    		CardContainer mainRow = indexPage.mainRows.get(i);
    		String row = String.format(MAIN_ROW_FMT1, i);
    		assertThatTheCorrectCardsAreFound(row, mainRow.getCards());
    	}
    }
    
    @Test
    public void should_find_orphan_card() {
    	Card card = indexPage.orphanCard;
    	assertThatTheCorrectCardIsFound(ORPHAN_ROW, 0, card);
    }
    
    private void assertThatTheCorrectCardsAreFound(String row, List<Card> cards) {
    	assertNotNull(cards);
    	assertThat(cards, is(not(empty())));
    	for (int i = 0; i < cards.size(); i++) {
    		Card card = cards.get(i);
    		assertThatTheCorrectCardIsFound(row, i, card);
    	}
    }

    private void assertThatTheCorrectCardIsFound(String row, int index, Card card) {
    	assertNotNull(card);
		assertThat(card.getTitle(), is(expectedTitle(row, index)));
		assertThat(card.getContent(), is(expectedContent(row, index)));
    }
    
    private static String expectedTitle(String rowName, int index) {
    	return String.format(TITLE_FMT2, rowName, index);
    }
    
    private static String expectedContent(String rowName, int index) {
    	return String.format(CONTENT_FMT2, rowName, index);
    }
}

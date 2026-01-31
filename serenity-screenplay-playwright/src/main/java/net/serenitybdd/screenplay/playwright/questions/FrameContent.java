package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;

/**
 * Questions about content within an iframe.
 */
public class FrameContent {

    private final String frameSelector;

    private FrameContent(String frameSelector) {
        this.frameSelector = frameSelector;
    }

    /**
     * Access content within the specified frame.
     */
    public static FrameContent inFrame(String frameSelector) {
        return new FrameContent(frameSelector);
    }

    /**
     * Get the text content of an element within the frame.
     */
    public Question<String> textOf(String elementSelector) {
        return new FrameTextQuestion(frameSelector, elementSelector);
    }

    /**
     * Get the input value of an element within the frame.
     */
    public Question<String> valueOf(String elementSelector) {
        return new FrameInputValueQuestion(frameSelector, elementSelector);
    }

    /**
     * Check if an element is visible within the frame.
     */
    public Question<Boolean> isVisible(String elementSelector) {
        return new FrameVisibilityQuestion(frameSelector, elementSelector);
    }

    /**
     * Get the count of matching elements within the frame.
     */
    public Question<Integer> countOf(String elementSelector) {
        return new FrameCountQuestion(frameSelector, elementSelector);
    }

    /**
     * Get the attribute value of an element within the frame.
     */
    public Question<String> attributeOf(String elementSelector, String attributeName) {
        return new FrameAttributeQuestion(frameSelector, elementSelector, attributeName);
    }

    /**
     * Get text from all matching elements within the frame.
     */
    public Question<List<String>> allTextsOf(String elementSelector) {
        return new FrameAllTextsQuestion(frameSelector, elementSelector);
    }
}

@Subject("the text of #elementSelector within frame #frameSelector")
class FrameTextQuestion implements Question<String> {
    private final String frameSelector;
    private final String elementSelector;

    FrameTextQuestion(String frameSelector, String elementSelector) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        return frame.locator(elementSelector).textContent();
    }
}

@Subject("the value of #elementSelector within frame #frameSelector")
class FrameInputValueQuestion implements Question<String> {
    private final String frameSelector;
    private final String elementSelector;

    FrameInputValueQuestion(String frameSelector, String elementSelector) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        return frame.locator(elementSelector).inputValue();
    }
}

@Subject("whether #elementSelector is visible within frame #frameSelector")
class FrameVisibilityQuestion implements Question<Boolean> {
    private final String frameSelector;
    private final String elementSelector;

    FrameVisibilityQuestion(String frameSelector, String elementSelector) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        return frame.locator(elementSelector).isVisible();
    }
}

@Subject("the count of #elementSelector within frame #frameSelector")
class FrameCountQuestion implements Question<Integer> {
    private final String frameSelector;
    private final String elementSelector;

    FrameCountQuestion(String frameSelector, String elementSelector) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
    }

    @Override
    public Integer answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        return frame.locator(elementSelector).count();
    }
}

@Subject("the #attributeName of #elementSelector within frame #frameSelector")
class FrameAttributeQuestion implements Question<String> {
    private final String frameSelector;
    private final String elementSelector;
    private final String attributeName;

    FrameAttributeQuestion(String frameSelector, String elementSelector, String attributeName) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
        this.attributeName = attributeName;
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        return frame.locator(elementSelector).getAttribute(attributeName);
    }
}

@Subject("all texts of #elementSelector within frame #frameSelector")
class FrameAllTextsQuestion implements Question<List<String>> {
    private final String frameSelector;
    private final String elementSelector;

    FrameAllTextsQuestion(String frameSelector, String elementSelector) {
        this.frameSelector = frameSelector;
        this.elementSelector = elementSelector;
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        FrameLocator frame = page.frameLocator(frameSelector);
        return frame.locator(elementSelector).allTextContents();
    }
}

package net.thucydides.core.pages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenNamingHtmlTags {

    @Mock
    WebElement unknownElementType;

    @Mock
    WebElement button;

    @Mock
    WebElement buttonWithValue;

    @Mock
    WebElement submitButton;

    @Mock
    WebElement link;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        mockWebElement(unknownElementType).returnsTagAndText("unknown", "unknown text");
        mockWebElement(button).returnsTagTypeAndText("input", "button", "Cancel","submit");
        mockWebElement(buttonWithValue).returnsTagTypeAndText("input", "button", "","OK");
        mockWebElement(submitButton).returnsTagTypeAndText("input", "submit", "OK","");
        mockWebElement(link).returnsTagAndText("a", "Cancel");
    }

    private MockWebElementBuilder mockWebElement(WebElement webElement) {
        return new MockWebElementBuilder(webElement);
    }

    @Test
    public void should_render_unknown_tag_type_as_the_original_tag() {
        assertThat(HtmlTag.from(unknownElementType).inHumanReadableForm(), is("unknown 'unknown text'"));
    }

    @Test
    public void should_render_a_button_using_the_contained_text() {
        assertThat(HtmlTag.from(button).inHumanReadableForm(), is("button: input - submit 'Cancel'"));
    }

    @Test
    public void should_render_a_value_button_using_the_value() {
        assertThat(HtmlTag.from(buttonWithValue).inHumanReadableForm(), is("button: input - OK"));
    }

    @Test
    public void should_render_a_link_using_the_contained_text() {
        assertThat(HtmlTag.from(link).inHumanReadableForm(), is("link 'Cancel'"));
    }

    @Test
    public void should_render_a_submit_button_using_the_contained_text() {
        assertThat(HtmlTag.from(submitButton).inHumanReadableForm(), is("button: input 'OK'"));
    }


    private class MockWebElementBuilder {
        private final WebElement webElement;
        
        public MockWebElementBuilder(WebElement webElement) {
            this.webElement = webElement;
        }

        public void returnsTagAndText(String tag, String text) {
            when(webElement.getTagName()).thenReturn(tag);
            when(webElement.getText()).thenReturn(text);
        }

        public void returnsTagTypeAndText(String tag, String type, String text, String value) {
            when(webElement.getTagName()).thenReturn(tag);
            when(webElement.getAttribute("type")).thenReturn(type);
            when(webElement.getAttribute("value")).thenReturn(value);
            when(webElement.getText()).thenReturn(text);
        }
    }
}

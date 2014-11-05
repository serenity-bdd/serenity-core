package net.thucydides.core.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static net.thucydides.core.hamcrest.XMLMatchers.isSimilarTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenUsingTheXMLMatcherTest {

    @Test
    public void should_match_identical_xml_documents() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        assertThat(anXmlDocument, isSimilarTo(anXmlDocument));
    }

    @Test
    public void should_not_match_different_xml_documents() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String aDifferentXmlDocument = "<loan><item code='a'>Item A</item></loan>";
        assertThat(anXmlDocument, not(isSimilarTo(aDifferentXmlDocument)));
    }

    @Test
    public void should_not_match_invalid_xml_document() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String aDifferentXmlDocument = "<loan><item code='a'>Item A</item><>";
        assertThat(anXmlDocument, not(isSimilarTo(aDifferentXmlDocument)));
    }

    @Test
    public void should_ignore_different_quotes_for_matches() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String anotherXmlDocument = "<sale><item code=\"a\">Item A</item></sale>";
        assertThat(anXmlDocument, isSimilarTo(anotherXmlDocument));
    }

    @Test
    public void should_ignore_white_space_for_matches() {
        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String anotherXmlDocument = "<sale>  <item code='a'>Item A</item></sale>";
        assertThat(anXmlDocument, isSimilarTo(anotherXmlDocument));
    }

    @Test
    public void should_display_expected_xml_document_when_failing() {

        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";

        XMLIsSimilarMatcher matcher = new XMLIsSimilarMatcher(anXmlDocument);
        Description description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), is("an XML document equivalent to <sale><item code='a'>Item A</item></sale>"));
    }

    @Test
    public void should_display_the_error_message_if_available() {

        String anXmlDocument = "<sale><item code='a'>Item A</item></sale>";
        String aDifferentXmlDocument = "<loan><item code='a'>Item A</item></loan>";

        XMLIsSimilarMatcher matcher = new XMLIsSimilarMatcher(anXmlDocument);
        matcher.matchesSafely(aDifferentXmlDocument);
        Description description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), containsString("an XML document equivalent to <sale><item code='a'>Item A</item></sale>"));
        assertThat(description.toString(), containsString("[different]"));

    }

}

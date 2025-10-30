package net.thucydides.core.reports.html;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.NumericalFormatter;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.formatters.ReportFormatter;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.issues.IssueTracking;
import net.thucydides.model.reports.html.DescriptionSplitter;
import net.thucydides.model.requirements.reports.RenderMarkdown;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenFormattingForHTML {

    public static final String NEW_LINE = System.lineSeparator();
    private Locale currentLocale;

    @Mock
    IssueTracking issueTracking;

    @Before
    public void prepareFormatter() {
        MockitoAnnotations.initMocks(this);
        currentLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @After
    public void after() {
        Locale.setDefault(currentLocale);
    }

    @Test
    public void should_include_issue_tracking_link_using_a_shortened_url() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");

        ReportFormatter formatter = new ReportFormatter(issueTracking);
        String formattedValue = formatter.addLinks("Fixes issue #123");

        assertThat(formattedValue, is("Fixes issue <a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">#123</a>"));
    }

    @Test
    public void should_identify_titles_containing_issue_links() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");

        TestOutcome outcomeWithIssues = TestOutcome.forTestInStory("Fix for #123", Story.called("some story"));
        outcomeWithIssues.usingIssueTracking(issueTracking);

        TestOutcome outcomeWithNoIssues = TestOutcome.forTestInStory("A simple test", Story.called("some story"));
        outcomeWithNoIssues.usingIssueTracking(issueTracking);

        assertThat(outcomeWithIssues.isTitleWithIssues(), is(true));
        assertThat(outcomeWithNoIssues.isTitleWithIssues(), is(false));
    }

    @Test
    public void should_include_issue_tracking_link_using_a_full_url() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");

        ReportFormatter formatter = new ReportFormatter(issueTracking);
        String formattedValue = formatter.addLinks("Fixes issue ISSUE-123");

        assertThat(formattedValue, is("Fixes issue <a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">ISSUE-123</a>"));
    }

    @Test
    public void should_include_issue_tracking_link_for_both_full_and_shortened_ids() {
        when(issueTracking.getIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/{0}");
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/MYPROJECT-{0}");
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("Fixes issue #1 and MYPROJECT-2");

        assertThat(formattedValue, is("Fixes issue <a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/MYPROJECT-1\">#1</a> and <a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/MYPROJECT-2\">MYPROJECT-2</a>"));
    }

    @Test
    public void should_include_multiple_issue_tracking_links() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MY-PROJECT/browse/ISSUE-{0}");
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("A scenario with about issues #123 and #456");

        assertThat(formattedValue, is("A scenario with about issues <a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-123\">#123</a> and <a target=\"_blank\" href=\"http://my.issue.tracker/MY-PROJECT/browse/ISSUE-456\">#456</a>"));
    }

    @Test
    public void should_allow_letters_and_numbers_in_issue_number() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MYPROJECT/browse/{0}");
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("A big story (#MYPROJECT-123,#MYPROJECT-456)");

        assertThat(formattedValue, is("A big story (<a target=\"_blank\" href=\"http://my.issue.tracker/MYPROJECT/browse/MYPROJECT-123\">#MYPROJECT-123</a>,<a target=\"_blank\" href=\"http://my.issue.tracker/MYPROJECT/browse/MYPROJECT-456\">#MYPROJECT-456</a>)"));
    }

    @Test
    public void should_allow_overlapping_issue_number() {
        IssueTracking issueTracking = mock(IssueTracking.class);
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MYPROJECT/browse/{0}");
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("A big story (#MYPROJECT-12,#MYPROJECT-123,#MYPROJECT-1)");

        assertThat(formattedValue, containsString("http://my.issue.tracker/MYPROJECT/browse/MYPROJECT-1"));
        assertThat(formattedValue, containsString("http://my.issue.tracker/MYPROJECT/browse/MYPROJECT-12"));
        assertThat(formattedValue, containsString("http://my.issue.tracker/MYPROJECT/browse/MYPROJECT-123"));
    }

    @Test
    public void should_allow_dashes_in_issue_number() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MYPROJECT/browse/{0}");
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("A big story (#MY-PROJECT-123,#MY-PROJECT-456)");

        assertThat(formattedValue, is("A big story (<a target=\"_blank\" href=\"http://my.issue.tracker/MYPROJECT/browse/MY-PROJECT-123\">#MY-PROJECT-123</a>,<a target=\"_blank\" href=\"http://my.issue.tracker/MYPROJECT/browse/MY-PROJECT-456\">#MY-PROJECT-456</a>)"));
    }

    @Test
    public void should_allow_underscores_in_issue_number() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn("http://my.issue.tracker/MYPROJECT/browse/{0}");
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("A big story (#MY_PROJECT_123,#MY_PROJECT_456)");

        assertThat(formattedValue, is("A big story (<a target=\"_blank\" href=\"http://my.issue.tracker/MYPROJECT/browse/MY_PROJECT_123\">#MY_PROJECT_123</a>,<a target=\"_blank\" href=\"http://my.issue.tracker/MYPROJECT/browse/MY_PROJECT_456\">#MY_PROJECT_456</a>)"));
    }

    @Test
    public void formatter_should_render_markdown_by_default() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        Formatter formatter = new Formatter( environmentVariables);
        String formatted = formatter.renderDescription("a quick *brown* fox\njumped over a log");
        assertThat(formatted, containsString("a quick <em>brown</em> fox"));
    }

    @Test
    public void formatter_should_not_render_markdown_if_not_configured_for_narrative() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("enable.markdown","story");
        Formatter formatter = new Formatter(environmentVariables);
        String formatted = formatter.renderDescription("a quick *brown* fox\njumped over a log");
        assertThat(formatted, containsString("a quick *brown* fox"));
    }

    @Test
    public void formatter_should_render_markdown_headings_by_default() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        Formatter formatter = new Formatter(environmentVariables);
        String formatted = formatter.renderDescription("# Heading");
        assertThat(formatted, containsString("<h1>Heading</h1>"));
        formatted = formatter.renderDescription("## Sub-heading");
        assertThat(formatted, containsString("<h2>Sub-heading</h2>"));
        formatted = formatter.renderDescription("### Another deeper heading");
        assertThat(formatted, containsString("<h3>Another deeper heading</h3>"));
    }

    @Test
    public void formatter_should_not_render_markdown_headings_if_not_configured() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("enable.markdown","story");
        Formatter formatter = new Formatter(environmentVariables);
        String formatted = formatter.renderDescription("# Heading");
        assertThat(formatted, containsString("# Heading"));
        formatted = formatter.renderDescription("## Sub-heading");
        assertThat(formatted, containsString("## Sub-heading"));
        formatted = formatter.renderDescription("### Another deeper heading");
        assertThat(formatted, containsString("### Another deeper heading"));
    }

    @Test
    public void markdown_should_not_affect_escaped_html_characters() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        Formatter formatter = new Formatter( environmentVariables);
        String htmlFormatted = formatter.htmlCompatible(formatter.renderMarkdown("&Eacute;rintett befogad&aacute;sa"));

        assertThat(htmlFormatted, equalTo("&Eacute;rintett befogad&aacute;sa"));
    }

    @Test
    public void markdown_should_render_formatting() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        Formatter formatter = new Formatter( environmentVariables);
        String htmlFormatted = formatter.htmlCompatible(formatter.renderMarkdown("**bold**"));

        assertThat(htmlFormatted, equalTo("<strong>bold</strong>"));
    }

    @Test
    public void markdown_should_render_images() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        Formatter formatter = new Formatter( environmentVariables);
        String htmlFormatted = formatter.htmlCompatible(formatter.renderMarkdown("![Wordle Game](assets/wordle.png)"));

        assertThat(htmlFormatted, equalTo("<img src=\"assets/wordle.png\" alt=\"Wordle Game\" />"));
    }

    @Test
    public void markdown_should_render_resized_images() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        Formatter formatter = new Formatter( environmentVariables);
        String htmlFormatted = formatter.htmlCompatible(formatter.renderMarkdown("![Image_30x30](./test-image.jpg =30x30)"));

        assertThat(htmlFormatted, equalTo("<img src=\"./test-image.jpg\" alt=\"Image_30x30\" width=\"30px\" height=\"30px\"></img>"));
    }

    @Test
    public void markdown_should_render_tables() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        Formatter formatter = new Formatter( environmentVariables);
        String htmlFormatted = formatter.htmlCompatible(formatter.renderMarkdown(
                "| Tasks |" + System.lineSeparator() +
                "|-------|" + System.lineSeparator() +
                "| Bread |"));

        assertThat(htmlFormatted, containsString("<table>"));
    }


    private final String htmlDescription = "<h2><a name=\"ScenarioDosometests\"></a>Scenario Do some tests</h2>\n"+
            "<p><b>Given</b> we want to test some stuff<br/>\n"+
            "<b>When</b> we do some tests<br/>\n"+
            "<b>Then</b> the stuff should be tested<br/>\n"+
            "<b>Examples</b></p>\n"+
            "<table class='confluenceTable'><tbody>\n"+
            "<tr>\n"+
            "<td class='confluenceTd'> test     </td>\n"+
            "<td class='confluenceTd'> expected </td>\n"+
            "</tr>\n"+
            "<tr>\n"+
            "<td class='confluenceTd'> Test 1 </td>\n"+
            "<td class='confluenceTd'> success  </td>\n"+
            "</tr>\n"+
            "<tr>\n"+
            "<td class='confluenceTd'> Test 2 </td>\n"+
            "<td class='confluenceTd'> failure     </td>\n"+
            "</tr>\n"+
            "</tbody></table>\n";

    @Test
    public void formatter_should_leave_rendered_html_as_is() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        Formatter formatter = new Formatter(environmentVariables);
        String formatted = formatter.renderDescription(htmlDescription);
        assertThat(formatted, is(htmlDescription));
    }

    @Test
    public void formatter_should_convert_simple_html_tags_in_titles() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        Formatter formatter = new Formatter(environmentVariables);
        String formatted = formatter.renderTitle("some <tag>");
        assertThat(formatted, is("some &lt;tag&gt;"));
    }


    @Test
    public void formatter_should_convert_empty_html_tags_in_titles() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        Formatter formatter = new Formatter(environmentVariables);
        String formatted = formatter.renderTitle("Some title including <> in it");
        assertThat(formatted, is("Some title including &lt;&gt; in it"));
    }

    @Test
    public void formatter_should_respect_capitals_in_titles() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        Formatter formatter = new Formatter(environmentVariables);
        String formatted = formatter.renderTitle("Some Title Including <> IN IT");
        assertThat(formatted, is("Some Title Including &lt;&gt; IN IT"));
    }

    @Test
    public void should_identify_issues_in_a_text() {
        List<String> issues = ReportFormatter.shortenedIssuesIn("A scenario about issue #123");

        assertThat(issues, hasItem("#123"));
    }

    @Test
    public void should_identify_multiple_issues_in_a_text() {
        List<String> issues = ReportFormatter.shortenedIssuesIn("A scenario about issue #123,#456, #789");

        assertThat(issues, hasItems("#123", "#456", "#789"));
    }

    @Test
    public void should_not_format_issues_if_no_issue_manage_url_is_provided() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("A scenario about issues #123 and #456");

        assertThat(formattedValue, is("A scenario about issues #123 and #456"));
    }

    @Test
    public void should_not_format_issues_if_the_issue_manage_url_is_empty() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        ReportFormatter formatter = new ReportFormatter(issueTracking);

        String formattedValue = formatter.addLinks("A scenario with about issues #123 and #456");

        assertThat(formattedValue, is("A scenario with about issues #123 and #456"));
    }

    @Test
    public void should_insert_line_breaks_into_text_values() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.htmlCompatible("Line one\nLine two\nLine three");

        assertThat(formattedValue, is("Line one<br>Line two<br>Line three"));
    }

    @Test
    public void should_convert_text_tables_into_html_tables() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.convertAnyTables("| name | age |\n|Bill|20|");

        assertThat(formattedValue, is("<table class='embedded'><thead><th>name</th><th>age</th></thead><tbody><tr><td>Bill</td><td>20</td></tr></tbody></table>"));
    }

    @Test
    public void should_convert_single_row_text_tables_into_html_tables() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.convertAnyTables("|Bill|20|");

        assertThat(formattedValue, is("<table class='embedded'><tbody><tr><td>Bill</td><td>20</td></tr></tbody></table>"));
    }

    @Test
    public void should_convert_embedded_text_tables_into_html_tables() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.convertAnyTables("A table:\n| name | age |\n|Bill|20|");

        assertThat(formattedValue, is("A table:<br><table class='embedded'><thead><th>name</th><th>age</th></thead><tbody><tr><td>Bill</td><td>20</td></tr></tbody></table>"));
    }


    @Test
    public void should_convert_single_column_embedded_text_tables_into_html_tables_without_a_header_row() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.convertAnyTables("A table:\n| Rob |\n|Bill|");

        assertThat(formattedValue, is("A table:<br><table class='embedded'><tbody><tr><td>Rob</td></tr><tr><td>Bill</td></tr></tbody></table>"));
    }
    @Test
    public void should_convert_embedded_text_tables__with_square_brackets_into_html_tables() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.convertAnyTables("A table:\n[| name | age |\n|Bill|20|]");

        assertThat(formattedValue, is("A table:<br><table class='embedded'><thead><th>name</th><th>age</th></thead><tbody><tr><td>Bill</td><td>20</td></tr></tbody></table>"));
    }

    @Test
    public void should_convert_embedded_jbehave_style_tables__with_square_brackets_into_html_tables() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.convertAnyTables("Given I have the following pet:\n［|name | status |\n|Fido | available |］");

        assertThat(formattedValue, is("Given I have the following pet:<br><table class='embedded'><thead><th>name</th><th>status</th></thead><tbody><tr><td>Fido</td><td>available</td></tr></tbody></table>"));
    }


    @Test
    public void should_ignore_isolated_pipes() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.convertAnyTables("Not a table: | that was a pipe");

        assertThat(formattedValue, is("Not a table: | that was a pipe"));
    }


    @Test
    public void should_return_empty_string_when_inserting_line_breaks_into_a_null_value() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.htmlCompatible(null);

        assertThat(formattedValue, is(""));
    }

    @Test
    public void should_insert_line_breaks_into_text_values_with_windows_line_breaks() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.htmlCompatible("Line one\r\nLine two\r\nLine three");

        assertThat(formattedValue, is("Line one<br>Line two<br>Line three"));
    }

    @Test
    public void should_not_escape_html_tags() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.htmlCompatible("<ul style='margin-left:5%'><li>Line one</li><li>Line two</li><li>Line three</li></ul>");

        assertThat(formattedValue.trim(), is("<ul style='margin-left:5%'><li>Line one</li><li>Line two</li><li>Line three</li></ul>"));
    }

    @Test
    public void should_not_escape_xml_tags_unless_asked_nicely() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.renderText("<wsse:username>nonofyourbusiness</wsse:username>");

        assertThat(formattedValue, is("&lt;wsse:username&gt;nonofyourbusiness&lt;/wsse:username&gt;"));
    }

    @Test
    public void should_keep_new_line_chars_in_xml() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.renderText("<catalog>\n" +
                "    <cd>\n" +
                "        <title>Empire Burlesque</title>\n" +
                "        <artist>Bob Dylan</artist>\n" +
                "        <country>USA</country>\n" +
                "        <country>Columbia</country>\n" +
                "        <price>10.90</price>\n" +
                "        <year>1985</year>\n" +
                "    </cd>\n" +
                "</catalog>");

        assertThat(formattedValue, is("&lt;catalog&gt;<br>    " +
                "&lt;cd&gt;<br>        " +
                "&lt;title&gt;Empire Burlesque&lt;/title&gt;<br>" +
                "        &lt;artist&gt;Bob Dylan&lt;/artist&gt;<br>" +
                "        &lt;country&gt;USA&lt;/country&gt;<br>  " +
                "      &lt;country&gt;Columbia&lt;/country&gt;<br> " +
                "       &lt;price&gt;10.90&lt;/price&gt;<br>  " +
                "      &lt;year&gt;1985&lt;/year&gt;<br> " +
                "   &lt;/cd&gt;<br>&lt;/catalog&gt;"));
    }

    @Test
    public void should_keep_new_line_chars_in_json() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.renderText("{\n" +
                "    \"id\": 1409959379,\n" +
                "    \"name\": \"Fido\",\n" +
                "    \"photoUrls\": [],\n" +
                "    \"tags\": [],\n" +
                "    \"status\": \"available\"\n" +
                "}");

        assertThat(formattedValue, is("{<br>    &quot;id&quot;: 1409959379,<br>" +
                "    &quot;name&quot;: &quot;Fido&quot;,<br> " +
                "   &quot;photoUrls&quot;: [],<br> " +
                "   &quot;tags&quot;: [],<br> " +
                "   &quot;status&quot;: &quot;available&quot;<br>}"));
    }

    @Test
    public void should_keep_utf8_new_line_chars_in_json() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.renderText("{␤" +
                "    \"id\": 1409959379,␤" +
                "    \"name\": \"Fido\",␤" +
                "    \"photoUrls\": [],␤" +
                "    \"tags\": [],␤" +
                "    \"status\": \"available\"␤" +
                "}");

        assertThat(formattedValue, is("{<br>    &quot;id&quot;: 1409959379,<br>" +
                "    &quot;name&quot;: &quot;Fido&quot;,<br> " +
                "   &quot;photoUrls&quot;: [],<br> " +
                "   &quot;tags&quot;: [],<br> " +
                "   &quot;status&quot;: &quot;available&quot;<br>}"));
    }

    @Test
    public void should_keep_escaped_unicode() {
        when(issueTracking.getShortenedIssueTrackerUrl()).thenReturn(null);
        Formatter formatter = new Formatter();

        String formattedValue = formatter.renderText("\\u0008");

        assertThat(formattedValue, is("&#92;0008"));
    }


    @Test
    public void should_escape_table_fields() {
        Formatter formatter = new Formatter();

        List<String> fields = NewList.of("name","age");
        String formattedValue = formatter.formatWithFields("Given a person named <name>\nand aged <age>");

        assertThat(formattedValue, is("Given a person named &lt;name&gt;<br>and aged &lt;age&gt;"));
    }

    @Test
    public void should_disable_markdown_formatting_if_configured() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("enable.markdown", "story");
        Formatter formatter = new Formatter(environmentVariables);

        String formattedValue = formatter.formatWithFields("Given a **person** named _Joe_");

        assertThat(formattedValue, is("Given a **person** named _Joe_"));
    }

    @Test
    public void should_allow_markdown_formatting_if_configured() {
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("enable.markdown", "step");
        Formatter formatter = new Formatter(environmentVariables);

        String formattedValue = formatter.formatWithFields("Given a **person** named _Joe_");

        assertThat(formattedValue, is("Given a <strong>person</strong> named <em>Joe</em>"));
    }

    @Test
    public void formatter_should_round_doubles_to_a_given_precision() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.234,1), is("1.2"));
    }

    @Test
    public void formatter_should_round_doubles_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.234,0), is("1"));
    }

    @Test
    public void formatter_should_round_doubles_up_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.634,0), is("2"));
    }

    @Test
    public void formatter_should_round_doubles_up() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.678,1), is("1.7"));
    }

    @Test
    public void formatter_should_drop_training_zeros() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.rounded(1.0,2), is("1"));
    }

    @Test
    public void formatter_should_round_percentages_to_a_given_precision() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.1234,1), is("12.3%"));
    }

    @Test
    public void formatter_should_round_percentages_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.1234,0), is("12%"));
    }

    @Test
    public void formatter_should_round_percentages_up_to_zero_precision_if_required() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.1254,0), is("13%"));
    }

    @Test
    public void formatter_should_round_percentages_up() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.16789, 1), is("16.8%"));
    }

    @Test
    public void formatter_should_drop_training_zeros_for_percentages() {
        NumericalFormatter formatter = new NumericalFormatter();
        assertThat(formatter.percentage(0.5, 1), is("50%"));
    }

    @Test
    public void formatter_should_expand_embedded_tables_to_markdown_compatible_form() {
        Formatter formatter = new Formatter();
        String description = "Given a business with the following details:\n" +
                             "\n" +
                             "\n" +
                             "| Name | Category |\n" +
                             "|---|---|\n" +
                             "| ACME | Casino |\n";


        assertThat(formatter.renderHtmlEscapedDescription(description), containsString("<table"));
    }

    @Test
    public void formatter_should_prepare_tables_for_markdown() {
        Formatter formatter = new Formatter();
        String description = "Given a business with the following details:\n" +
                "| Name | Category |\n" +
                "|---|---|\n" +
                "| ACME | Casino |\n";

        assertThat(formatter.renderHtmlEscapedDescription(description), containsString("<table"));
    }

    @Test
    public void formatter_should_inject_lines_before() {
        String description = "Given a business with the following details:\n" +
                "| Name | Category |\n" +
                "|---|---|\n" +
                "| ACME | Casino |\n";

        assertThat(RenderMarkdown.preprocessMarkdownTables(description), equalTo(
                "Given a business with the following details:" + NEW_LINE + NEW_LINE + NEW_LINE +
                "| Name | Category |" + NEW_LINE +
                "|---|---|" + NEW_LINE +
                "| ACME | Casino |"));
    }

    @Test
    public void formatter_should_handle_jbehave_style_tables() {
        String description = "Given a business with the following details:\n" +
                "［| Name | Category |\n" +
                "|---|---|\n" +
                "| ACME | Casino |］\n";

        assertThat(RenderMarkdown.preprocessMarkdownTables(description), equalTo(
                "Given a business with the following details:" + NEW_LINE + NEW_LINE + NEW_LINE +
                        "| Name | Category |" + NEW_LINE +
                        "|---|---|" + NEW_LINE +
                        "| ACME | Casino |"));
    }

    @Test
    public void should_split_up_given_when_then_statements() {
        String statement = "Given a calculator\n" +
                "When I give the following instructions:\n" +
                "Then system should display 6\n";

        assertThat(DescriptionSplitter.splitIntoSteps(statement), hasItems("Given a calculator",
                                                                        "When I give the following instructions:",
                                                                        "Then system should display 6"));
    }

    @Test
    public void should_inlcude_tables_when_spliting_up_given_when_then_statements() {
        String statement = "Given a calculator\n" +
                "When I give the following instructions:\n" +
                "| Operation | Amount |\n" +
                "| + | 10 |\n" +
                "| - | 5|\n" +
                "Then system should display 6\n";

        assertThat(DescriptionSplitter.splitIntoSteps(statement), hasItems("Given a calculator",
                "When I give the following instructions:" + NEW_LINE +
                "| Operation | Amount |" + NEW_LINE +
                        "| + | 10 |" + NEW_LINE +
                        "| - | 5|",
                "Then system should display 6"));

    }

}

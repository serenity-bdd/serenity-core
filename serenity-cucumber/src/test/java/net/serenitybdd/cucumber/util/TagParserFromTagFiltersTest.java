package net.serenitybdd.cucumber.util;

import io.cucumber.tagexpressions.Expression;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TagParserFromTagFiltersTest {

    @Test
    public void startSimpleWithASingleExclude() {
        Expression expression = TagParser.parseFromTagFilters(asList("not @wip"));
        assertThat(expression.evaluate(asList("@wip")), is(false));
        assertThat(expression.evaluate(asList("@wip", "@smoke")), is(false));
    }

    @Test
    public void shouldSupportOldSyntaxInCasePeopleStillUseThat() {
        Expression expression = TagParser.parseFromTagFilters(asList("~@wip"));
        assertThat(expression.evaluate(asList("@wip")), is(false));
        assertThat(expression.evaluate(asList("@wip", "@smoke")), is(false));
        assertThat(expression.evaluate(asList("@smoke")), is(true));
    }

    @Test
    public void shouldSupportAMixedList() {
        List<String> stringList = asList("not @wip", "@AGENT_WEB_ACC_TESTS", "@smoke", "not @dont_run_me");
        Expression expression = TagParser.parseFromTagFilters(stringList);
        assertThat(expression.evaluate(asList("@wip")), is(false));
        assertThat(expression.evaluate(asList("@wip", "@smoke")), is(false));
        assertThat(expression.evaluate(asList("@dont_run_me")), is(false));
        assertThat(expression.evaluate(asList("@dont_run_me", "@AGENT_WEB_ACC_TESTS")), is(false));
        assertThat(expression.evaluate(asList("@AGENT_WEB_ACC_TESTS", "@smoke")), is(true));
    }

    @Test
    public void whenProcessingAnEmptyArgumentEverythingIsRun() {
        Expression expression = TagParser.parseFromTagFilters(asList());
        assertThat(expression.evaluate(asList("@wip")), is(true));
        assertThat(expression.evaluate(asList("@wip", "@smoke")), is(true));
        assertThat(expression.evaluate(asList("@dont_run_me")), is(true));
        assertThat(expression.evaluate(asList("@dont_run_me", "@AGENT_WEB_ACC_TESTS")), is(true));
        assertThat(expression.evaluate(asList("@AGENT_WEB_ACC_TESTS")), is(true));
    }

    @Test
    public void eachItemInListWillBeJoinedWithAnd() {
        Expression expression = TagParser.parseFromTagFilters(asList("@this", "@AGENT_WEB_ACC_TESTS", "@smoke", "@do_run_me"));
        assertThat(expression.evaluate(asList("@wip")), is(false));
        assertThat(expression.evaluate(asList("@this", "@AGENT_WEB_ACC_TESTS", "@smoke", "@do_run_me")), is(true));
        assertThat(expression.evaluate(asList("@do_run_me", "@this", "@AGENT_WEB_ACC_TESTS", "@smoke")), is(true));
        assertThat(expression.evaluate(asList("@dont_run_me")), is(false));
        assertThat(expression.evaluate(asList("@AGENT_WEB_ACC_TESTS")), is(false));
    }

    @Test
    public void shouldSupportTagsJoinedByOrInASingleArgument() {
        Expression expression = TagParser.parseFromTagFilters(asList("@smoke or @my_health_coc or @ManageFeatureToggles"));
        assertThat(expression.evaluate(asList("@smoke")), is(true));
        assertThat(expression.evaluate(asList("@smoke", "@my_health_coc", "@ManageFeatureToggles")), is(true));
        assertThat(expression.evaluate(asList("@snot")), is(false));
        assertThat(expression.evaluate(asList("@smoke", "@snot", "@ManageFeatureToggles")), is(true));
    }

    @Test
    public void shouldSupportTagsJoinedByOrInAMultipleArgumentsAndJoinThemWithAnd() {
        Expression expression = TagParser.parseFromTagFilters(asList("@tag1 or @tag2 or @tag3", "@taga or @tagb or @tagc"));
        assertThat(expression.evaluate(asList("@tag1")), is(false));
        assertThat(expression.evaluate(asList("@tag2", "@tagb")), is(true));
        assertThat(expression.evaluate(asList("@tag1", "@tagb", "@tag2", "@taga")), is(true));
    }
}

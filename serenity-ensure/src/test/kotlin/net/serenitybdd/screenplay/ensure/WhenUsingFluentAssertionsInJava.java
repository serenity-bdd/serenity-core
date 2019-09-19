package net.serenitybdd.screenplay.ensure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.GivenWhenThen;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.ensure.web.ElementsLocated;
import net.serenitybdd.screenplay.ensure.web.NamedExpectation;
import net.serenitybdd.screenplay.ensure.web.TheMatchingElement;
import net.serenitybdd.screenplay.questions.NamedPredicate;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(SerenityRunner.class)
/**
 * Some high level smoke tests
 */
public class WhenUsingFluentAssertionsInJava {

    Actor aster = Actor.named("Aster");

    @Test
    public void weCanMakeAssertionsAboutIntegers() {

        int age = 20;

        aster.attemptsTo(
                Ensure.that(age).isEqualTo(20)
        );
    }

    @Test
    public void weCanMakeAssertionsAboutFloats() {
        Actor aster = Actor.named("Aster");

        float creditScore = 9.8F;

        aster.attemptsTo(
                Ensure.that(creditScore).isCloseTo(9.81F, 0.01F)
        );
    }

    @Test
    public void weCanMakeAssertionsAboutApproximateDoubles() {
        Actor aster = Actor.named("Aster");

        double score = 9.8;

        aster.attemptsTo(
                Ensure.that(score).isCloseTo(9.81, 0.01)
        );
    }

    @Test
    public void weCanMakeAssertionsAboutCollections() {
        Actor aster = Actor.named("Aster");

        List<String> colors = Arrays.asList("red", "green", "blue");

        aster.attemptsTo(
                Ensure.that(colors).isNotNull(),
                Ensure.that("red").isIn(colors)
        );
    }

    @Test
    public void weCanMakeAssertionsAboutEmptyCollections() {
        Actor aster = Actor.named("Aster");

        List<String> nullcolors = null;

        aster.attemptsTo(
                Ensure.that(nullcolors).isNull()
        );
    }

    @Test
    public void weCanMakeAssertionsAboutDoubles() {
        Actor aster = Actor.named("Aster");

        double PI = 3.14195;

        aster.attemptsTo(
                Ensure.that(PI).isGreaterThan(3.0)
        );

    }

    @Test
    public void weCanMakeAssertionsUsingLambdas() {
        Actor aster = Actor.named("Aster");

        String actualColor = "green";

        aster.attemptsTo(
                Ensure.that(actualColor).matches("is an RGB color",
                                          color -> color.equals("red") || color.equals("blue") || color.equals("green")),
                Ensure.that(actualColor).not().matches("is red", color -> color.equals("red"))
        );
    }

    @Test
    public void weCanMakeAssertionsAboutStrings() {
        Actor aster = Actor.named("Aster");

        String name = "Bill";
        aster.attemptsTo(
                Ensure.that(name).isEqualToIgnoringCase("BILL")
        );
    }


    @Test
    public void weCanPassNullValues() {
        Actor aster = Actor.named("Aster");

        String nullName = null;

        aster.attemptsTo(
                Ensure.that(nullName).isNullOrEmpty()
        );
    }

    @Test
    public void weCanProvideCustomComparators() {
        Comparator<String> byLength = Comparator.comparingInt(String::length);

        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that("aardvark").usingComparator(byLength).isGreaterThan("cat")
        );
    }

    enum Color {RED, GREEN, BLUE}

    @Test
    public void weCanCompareEnums() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that(Color.RED).isLessThan(Color.BLUE),
                Ensure.that(Color.BLUE).usingComparator(Comparator.reverseOrder()).isLessThan(Color.RED)
        );
    }

    @Test
    public void weCanCompareBooleansAsStrings() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that("true").asABoolean().isTrue(),
                Ensure.that("false").asABoolean().isFalse()
        );
    }

    public void weCanCompareBooleans() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that(true).isTrue(),
                Ensure.that(false).isFalse()
        );
    }


    @Test
    public void weCanCompareDoubles() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that(3.14159d).isGreaterThan(3.0d)
        );
    }

    @Test
    public void weCanCompareFloats() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that(3.14159f).isGreaterThan(3.0f)
        );
    }


    @Test
    public void weCanCompareBigDecimals() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.that(new BigDecimal("3.14159")).isGreaterThan(BigDecimal.ONE)
        );
    }

    @Test
    public void weCanCompareElementsInAListAgainstAPredicate() {
        Actor aster = Actor.named("Aster");
        List<String> colors = ImmutableList.of("blue", "cyan", "pink");

        aster.attemptsTo(
                Ensure.that(colors).allMatch("4 characters long", it -> it.length() == 4)
        );
    }

//    private NamedPredicate<String> IS_A_PRIMARY_COLOR
//            = GivenWhenThen.returnsAValueThat("is a primary color",
//                                              color -> (color == "red") || (color == "green") || (color == "blue"));

    private static final  NamedExpectation<String> IS_A_PRIMARY_COLOR
            = new NamedExpectation<>("is a primary color",
                                   color -> (color.equals("red")) || (color.equals("green")) || (color.equals("blue")));

    @Test
    public void weCanCheckThatAnyElementMatchesAConditionWithANamedPredicate() {
        Actor aster = Actor.named("Aster");
        List<String> colors = ImmutableList.of("blue", "cyan", "pink");

        aster.attemptsTo(
                Ensure.that(colors).anyMatch(IS_A_PRIMARY_COLOR)
        );
    }

    @Test
    public void weCanCheckThatAnyElementMatchesACondition() {
        Actor aster = Actor.named("Aster");
        List<String> colors = ImmutableList.of("blue", "cyan", "pink");

        aster.attemptsTo(
                Ensure.that(colors).anyMatch("is a primary color", it ->  isAPrimaryColor(it))
        );
    }

    @Test
    public void weCanCheckThatNoElementMatchesACondition() {
        Actor aster = Actor.named("Aster");
        List<String> colors = ImmutableList.of("orange", "cyan", "pink");

        aster.attemptsTo(
                Ensure.that(colors).noneMatch("is a primary color", it ->  isAPrimaryColor(it))
        );
    }

    private boolean isAPrimaryColor(String color) {
        return  (color == "red") || (color == "green") || (color == "blue");
    }



    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutTextValues() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswerTo("the color red", colorRed()).asAString().isEqualTo("RED")
        );
    }

    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutEnums() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswerTo(statusOf("some-todo-item")).isEqualTo(TodoStatus.COMPLETED)
        );
    }

    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutNumbers() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswerTo("the count", countOf("some-todo-item")).isEqualTo(1)
        );
    }

    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutCollections() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswersTo(colors()).contains("red")
        );
    }

    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutBooleansWithDescriptions() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswerTo("the boolean", booleanEquivalentOf("true")).isTrue()
        );
    }

    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutBooleans() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswerTo(booleanEquivalentOf("true")).isTrue()
        );
    }

    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutBooleanStrings() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswerTo("the boolean", stringBooleanEquivalentOf("true")).asABoolean().isTrue(),
                Ensure.thatTheAnswerTo(stringBooleanEquivalentOf("true")).asABoolean().isTrue()
        );
    }

    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutStrings() {
        Actor aster = Actor.named("Aster");

        aster.attemptsTo(
                Ensure.thatTheAnswerTo("the string value", stringValueOf("red, blue, yellow")).doesNotContain("green"),
                Ensure.thatTheAnswerTo(stringValueOf("red, blue, yellow")).doesNotContain("green")
        );
    }

    @Test
    public void weCanMakeAssertionsAboutTimes() {
        Actor aster = Actor.named("Aster");

        LocalTime tenInTheMorning = LocalTime.of(10,0);
        LocalTime twoInTheAfternoon = LocalTime.of(14,0);

        aster.attemptsTo(
                Ensure.that(tenInTheMorning).isBefore(twoInTheAfternoon),
                Ensure.that(twoInTheAfternoon).isAfter(tenInTheMorning)
        );
    }


    @Test
    public void weCanMakeAssertionsAboutDates() {
        Actor aster = Actor.named("Aster");

        LocalDate firstOfJanuary = LocalDate.of(2000,1,1);
        LocalDate secondOfJanuary = LocalDate.of(2000,1,2);

        aster.attemptsTo(
                Ensure.that(firstOfJanuary).isBefore(secondOfJanuary)
        );
    }


    @Test
    public void weCanMakeAssertionsAboutQuestionsAboutDate() {
        Actor aster = Actor.named("Aster");

        LocalDate firstOfJanuary = LocalDate.of(2000,1,1);
        LocalDate secondOfJanuary = LocalDate.of(2000,1,2);

        aster.attemptsTo(
                Ensure.thatTheAnswerTo("January 1st 2000", firstOfJanuary2000()).asADate().isBefore(secondOfJanuary),
                Ensure.thatTheAnswerTo("January 1st 2000", firstOfJanuary2000()).asADate().not().isBefore(firstOfJanuary)
        );
    }

    enum TodoStatus {COMPLETED, TODO}

    Question<TodoStatus> statusOf(String todoItem) {
        return Question.about("todo status").answeredBy(
                actor -> TodoStatus.COMPLETED
        );
    }

    public Question<Integer> countOf(String todoItem) {
        return Question.about("todo status").answeredBy(
                actor -> 1
        );
    }

    public Question<Boolean> booleanEquivalentOf(String todoItem) {
        return Question.about("a boolean field").answeredBy(
                actor -> Boolean.valueOf(todoItem)
        );
    }

    public Question<String> stringValueOf(String todoItem) {
        return Question.about("a string value").answeredBy(
                actor -> todoItem
        );
    }

    public Question<String> stringBooleanEquivalentOf(String todoItem) {
        return Question.about("a boolean field").answeredBy(
                actor -> todoItem
        );
    }

    Question<String> colorRed() {
        return Question.about("color red").answeredBy(
                actor -> "RED"
        );
    }

    Question<Collection<String>> colors() {
        return Question.about("colors").answeredBy(
                actor -> Arrays.asList("red","green","blue")
        );
    }

    Question<LocalDate> firstOfJanuary2000() {
        return Question.about("first of January").answeredBy(
                actor -> LocalDate.of(2000,1,1)
        );
    }

}

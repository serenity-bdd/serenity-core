package net.serenitybdd.assertions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CollectionMatchers {
    private final static Logger LOGGER = LoggerFactory.getLogger(CollectionMatchers.class);

    public static <E> Matcher<Collection<E>> containsAll(Collection<E> expectedListEntries) {
        return new TypeSafeMatcher<Collection<E>>() {
            @Override
            protected boolean matchesSafely(Collection<E> actualValues) {
                return actualValues.containsAll(expectedListEntries);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("contains ").appendValue(expectedListEntries);
            }
        };
    }

    public static <E> Matcher<Collection<E>> allMatch(Predicate<E> predicate) {
        return allMatch(predicate, "match predicate");
    }

    public static <E> Matcher<Collection<E>> allMatch(Predicate<E> predicate, String predicateDescription) {
        return new TypeSafeMatcher<Collection<E>>() {
            @Override
            protected boolean matchesSafely(Collection<E> actualValues) {
                return actualValues.stream().allMatch(predicate);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("all elements ").appendText(predicateDescription);
            }
        };
    }

    public static <E> Matcher<Collection<E>> anyMatch(Predicate<E> predicate) {
        return anyMatch(predicate,"match predicate");
    }

    public static <E> Matcher<Collection<E>> anyMatch(Predicate<E> predicate, String predicateDescription) {
        return new TypeSafeMatcher<Collection<E>>() {
            @Override
            protected boolean matchesSafely(Collection<E> actualValues) {
                return actualValues.stream().anyMatch(predicate);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("at least one element ").appendText(predicateDescription);
            }
        };
    }

    public static <E> Matcher<Collection<E>> allSatisfy(Consumer<E> consumer) {
        return allSatisfy(consumer, "all satisfy the expected condition");
    }

    public static <E> Matcher<Collection<E>> allSatisfy(Consumer<E> consumer, String consumerDescription) {
        return new TypeSafeMatcher<Collection<E>>() {
            @Override
            protected boolean matchesSafely(Collection<E> actualValues) {
                try {
                    actualValues.forEach(consumer);
                } catch (AssertionError assertionError) {
                    LOGGER.error("Assertion error", assertionError);
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("for all elements ").appendText(consumerDescription);
            }
        };
    }

    public static <E> Matcher<Collection<E>> satisfies(Consumer<Collection<E>> consumer) {
        return satisfies(consumer, "satisfies the expected condition");
    }

    public static <E> Matcher<Collection<E>> satisfies(Consumer<Collection<E>> consumer, String consumerDescription) {
        return new TypeSafeMatcher<Collection<E>>() {
            @Override
            protected boolean matchesSafely(Collection<E> actualValues) {
                try {
                    consumer.accept(actualValues);
                } catch (AssertionError assertionError) {
                    LOGGER.error("Assertion error", assertionError);
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("the collection ").appendText(consumerDescription);
            }
        };
    }
}

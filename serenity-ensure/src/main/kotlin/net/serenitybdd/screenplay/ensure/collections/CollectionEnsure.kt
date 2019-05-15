package net.serenitybdd.screenplay.ensure.collections

import net.serenitybdd.screenplay.ensure.*
import net.serenitybdd.screenplay.ensure.GrammaticalNumber.PLURAL
import net.serenitybdd.screenplay.ensure.GrammaticalNumber.SINGULAR


class CollectionEnsure<A>(val value: Collection<A>?, val comparator: Comparator<A>? = null) {

    /**
     * Verifies that the actual {@code Collection} is either an empty list or <code>null</code>
     */
    fun isNullOrEmpty() = PerformablePredicate(value, NULL_OR_EMPTY_LIST, isNegated())

    /**
     * Verifies that the actual {@code Collection} is an empty list
     */
    fun isEmpty() = PerformablePredicate(value, EMPTY_LIST, isNegated())

    /**
     * Verifies that the actual {@code Collection} contains at least one element
     */
    fun isNotEmpty() = PerformablePredicate(value, NOT_EMPTY_LIST, isNegated())

    /**
     * Verifies that the actual value is equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isEqualTo(&quot;abc&quot;));
     * </code></pre>
     */
    fun isEqualTo(expected: Collection<A>) = PerformableExpectation(value, IS_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual value is _not_ equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isEqualTo(&quot;123&quot;));
     * </code></pre>
     */
    fun isNotEqualTo(expected: Collection<A>) = PerformableExpectation(value, IS_NOT_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual value is {@code null}.
     * </code></pre>
     */
    fun isNull() = PerformablePredicate(value, IS_NULL_LIST, isNegated())

    /**
     * Verifies that the actual value is not {@code null}.
     */
    fun isNotNull() = PerformablePredicate(value, IS_NOT_NULL_LIST, isNegated())

    /**
     * Verifies that the actual {@code Collection} has the expected number of elements
     */
    fun hasSize(expected: Int) = PerformableExpectation(value, HAS_SIZE, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} has a size less than the given value.
     */
    fun hasSizeLessThan(expected: Int) = PerformableExpectation(value, HAS_SIZE_LESS_THAN, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} has a size less than or equal to the given value.
     */
    fun hasSizeLessThanOrEqualTo(expected: Int) = PerformableExpectation(value, HAS_SIZE_LESS_THAN_OR_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} has a size greater than the given value.
     */
    fun hasSizeGreaterThan(expected: Int) = PerformableExpectation(value, HAS_SIZE_GREATER_THAN, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} has has a size greater than the given value.
     */
    fun hasSizeGreaterThanOrEqualTo(expected: Int) = PerformableExpectation(value, HAS_SIZE_GREATER_THAN_OR_EQUAL_TO, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} has a size between the given boundaries (inclusive)
     * using the {@code length()} method.
     * */
    fun hasSizeBetween(startRange: Int, endRange: Int) = BiPerformableExpectation(value, HAS_SIZE_BETWEEN, startRange, endRange, isNegated())

    /**
     * Verifies that the actual {@code Collection} has a size that is the same as a specified ollection
     * using the {@code length()} method.
     * */
    fun hasSameSizeAs(expected: Collection<A>) = PerformableExpectation(value, HAS_SAME_SIZE_AS, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun contains(vararg expected: A) = PerformableExpectation(value, LIST_CONTAINS, expected.toList(), isNegated())

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun containsAnyOf(vararg expected: A) = PerformableExpectation(value, LIST_CONTAINS_ANY_OF, expected.toList(), isNegated())

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun containsAnyElementsOf(expected: Collection<A>) = PerformableExpectation(value, LIST_CONTAINS_ANY_OF, expected.toList(), isNegated())

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun containsElementsFrom(expected: Collection<A>) = PerformableExpectation(value, LIST_CONTAINS, expected, isNegated())

    /**
     * Verifies that the actual group contains only the given values and nothing else, in any order and ignoring duplicates (i.e. once a value is found, its duplicates are also considered found).
     */
    fun containsOnly(vararg expected: A) = PerformableExpectation(value, LIST_CONTAINS_ONLY, expected.toList(), isNegated())

    /**
     * Verifies that the actual group contains only the given values and nothing else, in any order and ignoring duplicates (i.e. once a value is found, its duplicates are also considered found).
     */
    fun containsOnlyElementsFrom(expected: Collection<A>) = PerformableExpectation(value, LIST_CONTAINS_ONLY, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} does not contain the given elements
     */
    fun doesNotContain(vararg expected: A) = PerformableExpectation(value, LIST_DOES_NOT_CONTAINS, expected.toList(), isNegated())

    /**
     * Verifies that the actual {@code Collection} does not contain the given elements
     */
    fun doesNotContainElementsFrom(expected: Collection<A>) = PerformableExpectation(value, LIST_DOES_NOT_CONTAINS, expected, isNegated())

    /**
     * Verifies that the actual {@code Collection} does not contain the given elements
     */
    fun doesNotHaveDuplicates() = PerformablePredicate(value, LIST_DOES_NOT_CONTAIN_DUPLICATES, isNegated())

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactly(vararg expected: A) = PerformableExpectation(value, LIST_CONTAINS_EXACTLY, expected.toList(), isNegated())

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactlyElementsFrom(expected: Collection<A>) = PerformableExpectation(value, LIST_CONTAINS_EXACTLY, expected, isNegated())

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactlyInAnyOrder(vararg expected: A) = PerformableExpectation(value, LIST_CONTAINS_EXACTLY_IN_ANY_ORDER, expected.toList(), isNegated())

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactlyInAnyOrderElementsFrom(expected: Collection<A>) = PerformableExpectation(value, LIST_CONTAINS_EXACTLY_IN_ANY_ORDER, expected, isNegated())

    /**
     * Verifies that all the elements of actual are present in the given values.
     */
    fun isASubsetOf(vararg expected: A) = PerformableExpectation(value, LIST_IS_A_SUBSET_OF, expected.toList(), isNegated())

    /**
     * Verifies that all the elements of actual are present in the given values.
     */
    fun isASubsetOf(expected: Collection<A>) = PerformableExpectation(value, LIST_IS_A_SUBSET_OF, expected, isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun startsWith(vararg expected: A) = PerformableExpectation(value, LIST_STARTS_WITH, expected.toList(), isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun startsWithElementsFrom(expected: Collection<A>) = PerformableExpectation(value, LIST_STARTS_WITH, expected, isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun endsWith(vararg expected: A) = PerformableExpectation(value, LIST_ENDS_WITH, expected.toList(), isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun endsWithElementsFrom(expected: Collection<A>) = PerformableExpectation(value, LIST_ENDS_WITH, expected, isNegated())

    /**
     * Verifies that every element in a collection matches a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     *
     * Example:
     * <pre><code>
     * Actor aster = Actor.named("Aster");
     * List<String> colors = ImmutableList.of("blue", "cyan", "pink");

     * aster.attemptsTo(
     *   Ensure.that(colors).allMatch("4 characters long", it -> it.length() == 4)
     * );
     */
    fun allMatch(predicateDescription: String, predicate: (A) -> Boolean) =
            // Each element is 4 characters long
            PerformablePredicate(value,
                    containsElementsThatAllMatch("each element",
                            SINGULAR,
                            ElementQualifier.IS_ARE,
                            predicateDescription,
                            predicate),
                    isNegated())


    /**
     * Verifies that at least one element in a collection matches a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun anyMatch(predicateDescription: String, predicate: (A) -> Boolean) =
            // Each element is 4 characters long
            PerformablePredicate(value,
                    containsAtLeastOneElementThatMatches("at least one element",
                            SINGULAR,
                            ElementQualifier.IS_ARE,
                            predicateDescription,
                            predicate),
                    isNegated())

    /**
     * Verifies that at least _n_ elements in a collection that match a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun atLeast(n: Int, predicateDescription: String, predicate: (A) -> Boolean): PerformablePredicate<Collection<A>> =
            PerformablePredicate(value,
                    containsAtLeastElementsThatMatch(n, "at least $n elements",
                            PLURAL,
                            ElementQualifier.IS_ARE,
                            predicateDescription,
                            predicate),
                    isNegated())

    /**
     * Verifies that no more than _n_ elements in a collection that match a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun noMoreThan(n: Int, predicateDescription: String, predicate: (A) -> Boolean): PerformablePredicate<Collection<A>> =
            PerformablePredicate(value,
                    containsAtMostElementsThatMatch(n, "at least $n elements",
                            PLURAL,
                            ElementQualifier.IS_ARE,
                            predicateDescription,
                            predicate),
                    isNegated())

    /**
     * Verifies that at no elements in a collection matches a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun noneMatch(predicateDescription: String, predicate: (A) -> Boolean) =
            PerformablePredicate(value,
                    containsNoElementsThatMatch("no elements",
                            PLURAL,
                            ElementQualifier.IS_ARE,
                            predicateDescription,
                            predicate),
                    isNegated())


    private var negated = false

    fun isNegated() = negated

    fun hasValue(): CollectionEnsure<A> = this

    fun not(): CollectionEnsure<A> {
        negated = !negated;
        return this
    }

    open fun usingComparator(comparator: Comparator<A>): CollectionEnsure<A> {
        return CollectionEnsure(value, comparator)
    }

    val EMPTY_LIST = expectThatActualIs("empty", fun(actual: Collection<A>?): Boolean = actual!!.isEmpty())
    val NOT_EMPTY_LIST = expectThatActualIs("not empty", fun(actual: Collection<A>?): Boolean = actual!!.isNotEmpty())
    val NULL_OR_EMPTY_LIST = expectThatActualIs("null or empty", fun(actual: Collection<A>?): Boolean = actual == null || actual.isEmpty())
    val IS_NOT_NULL_LIST = expectThatActualIs("not null", fun(actual: Collection<A>?): Boolean = actual != null)
    val IS_NULL_LIST = expectThatActualIs("null", fun(actual: Collection<A>?): Boolean = actual == null)
    val IS_EQUAL_TO = expectThatActualIs("equal to",
            fun(actual: Collection<A>?, expected: Collection<A>) = CollectionsComparison(comparator).areEqual(actual, expected)
    )
    val IS_NOT_EQUAL_TO = expectThatActualIs("not equal to",
            fun(actual: Collection<A>?, expected: Collection<A>) = !CollectionsComparison(comparator).areEqual(actual, expected)
    )

    val HAS_SIZE = expectThatActualIs("of size", fun(actual: Collection<A>?, expected: Int): Boolean = actual != null && actual.size == expected)
    val HAS_SIZE_LESS_THAN = expectThatActualIs("of size less than", fun(actual: Collection<A>?, expected: Int): Boolean = actual != null && actual.size < expected)
    val HAS_SIZE_LESS_THAN_OR_EQUAL_TO = expectThatActualIs("of size less than or equal to", fun(actual: Collection<A>?, expected: Int): Boolean = actual != null && actual.size <= expected)
    val HAS_SIZE_GREATER_THAN = expectThatActualIs("of size greater than", fun(actual: Collection<A>?, expected: Int): Boolean = actual != null && actual.size > expected)
    val HAS_SIZE_GREATER_THAN_OR_EQUAL_TO = expectThatActualIs("of size greater than or equal to", fun(actual: Collection<A>?, expected: Int): Boolean = actual != null && actual.size >= expected)
    val HAS_SIZE_BETWEEN = expectThatActualIs("of size of between", fun(actual: Collection<A>?, startRange: Int, endRange: Int): Boolean = actual != null && actual.size >= startRange && actual.size <= endRange)
    val HAS_SAME_SIZE_AS = expectThatActualIs("the same size as", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean = actual != null && expected != null && actual.size == expected.size)


    val LIST_CONTAINS = expectThatActualIs("a list containing", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean = actual != null && expected != null && actual.containsAll(expected))
    val LIST_DOES_NOT_CONTAINS = expectThatActualIs("a list not containing", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean = actual != null && expected != null && !actual.containsAll(expected))
    val LIST_CONTAINS_ONLY = expectThatActualIs("a list containing only", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false;
        return (actual.find { !expected.contains(it) } == null)
    })

    val LIST_CONTAINS_ANY_OF = expectThatActualIs("a list containing any of", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false;
        return (expected.find { actual.contains(it) } != null)
    })


    val LIST_CONTAINS_EXACTLY = expectThatActualIs("a list containing exactly", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false
        return (actual == expected)
    })

    val LIST_CONTAINS_EXACTLY_IN_ANY_ORDER = expectThatActualIs("a list containing exactly",
            fun(actual: Collection<A>?, expected: Collection<A>?): Boolean {
                if (actual == null || expected == null) return false
                return (actual.toSet() == expected.toSet())
            })

    val LIST_IS_A_SUBSET_OF = expectThatActualIs("a list containing exactly", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false
        return (actual.all { element -> expected.contains(element) })
    })

    val LIST_DOES_NOT_CONTAIN_DUPLICATES = expectThatActualIs("a list containing duplicates", fun(actual: Collection<A>?): Boolean {
        if (actual == null) return false
        return actual.toSet().size == actual.size
    })

    val LIST_STARTS_WITH = expectThatActualIs("a list starting with", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null || actual.size <= expected.size || expected.isEmpty()) return false
        return sequencesAreIdenticalIn(actual, expected, 0, expected.size - 1)
    })

    val LIST_ENDS_WITH = expectThatActualIs("a list ending with", fun(actual: Collection<A>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null || actual.size <= expected.size || expected.isEmpty()) return false
        val sequenceEnd = actual.size - 1
        val sequenceStart = sequenceEnd - expected.size + 1
        return sequencesAreIdenticalIn(actual, expected, sequenceStart, sequenceEnd)
    })

    private fun sequencesAreIdenticalIn(actual: Collection<A>,
                                        expected: Collection<A>,
                                        start: Int,
                                        end: Int): Boolean {
        for (i in start..end) {
            if (!elementsMatch(actual.elementAt(i), expected.elementAt(i - start))) return false
        }
        return true
    }

    private fun elementsMatch(actual: A, expected: A) = CollectionsComparison(comparator).isSame(actual, expected)

    private fun containsElementsThatAllMatch(overallDescription: String,
                                             predicateNumber: GrammaticalNumber,
                                             qualifier: ElementQualifier,
                                             predicateDescription: String,
                                             predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            overallDescription,
            fun(actual: Collection<A>?): Boolean = actual != null && actual.all(predicate),
            predicateDescription,
            qualifier,
            predicateNumber
    )

    private fun containsAtLeastOneElementThatMatches(overallDescription: String,
                                                     predicateNumber: GrammaticalNumber,
                                                     qualifier: ElementQualifier,
                                                     predicateDescription: String,
                                                     predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            overallDescription,
            fun(actual: Collection<A>?): Boolean = actual != null && actual.any(predicate),
            predicateDescription,
            qualifier,
            predicateNumber
    )

    private fun containsNoElementsThatMatch(overallDescription: String,
                                            predicateNumber: GrammaticalNumber,
                                            qualifier: ElementQualifier,
                                            predicateDescription: String,
                                            predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            overallDescription,
            fun(actual: Collection<A>?): Boolean = actual != null && actual.none(predicate),
            predicateDescription,
            qualifier,
            predicateNumber
    )

    private fun containsAtLeastElementsThatMatch(n: Int,
                                                 overallDescription: String,
                                                 predicateNumber: GrammaticalNumber,
                                                 qualifier: ElementQualifier,
                                                 predicateDescription: String,
                                                 predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            overallDescription,
            fun(actual: Collection<A>?): Boolean = actual != null && actual.filter(predicate).size >= n,
            predicateDescription,
            qualifier,
            predicateNumber
    )

    private fun containsAtMostElementsThatMatch(n: Int,
                                                 overallDescription: String,
                                                 predicateNumber: GrammaticalNumber,
                                                 qualifier: ElementQualifier,
                                                 predicateDescription: String,
                                                 predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            overallDescription,
            fun(actual: Collection<A>?): Boolean = actual != null && actual.filter(predicate).size <= n,
            predicateDescription,
            qualifier,
            predicateNumber
    )

}

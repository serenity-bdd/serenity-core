package net.serenitybdd.screenplay.ensure

import net.serenitybdd.markers.CanBeSilent
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.ElementQualifier.Companion.IS_ARE
import net.serenitybdd.screenplay.ensure.GrammaticalNumber.PLURAL
import net.serenitybdd.screenplay.ensure.GrammaticalNumber.SINGULAR
import net.serenitybdd.screenplay.ensure.collections.CollectionsComparison
import net.serenitybdd.screenplay.ensure.web.NamedExpectation

class CollectionEnsure<A>(val value: KnowableValue<Collection<A>?>,
                          private val targetDescription: String = "a collection",
                          val comparator: Comparator<A>? = null) : CanBeSilent {

    constructor(value: Collection<A>?) : this(KnownValue<Collection<A>?>(value, value.toString()))

    /**
     * Verifies that the actual {@code Collection} is either an empty list or <code>null</code>
     */
    fun isNullOrEmpty() = PerformablePredicate(value, nullOrEmptyList, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} is an empty list
     */
    fun isEmpty() = PerformablePredicate(value, emptyList, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} contains at least one element
     */
    fun isNotEmpty() = PerformablePredicate(value, notAnEmptyList, isNegated(),targetDescription)

    /**
     * Verifies that the actual value is equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'>
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isEqualTo(&quot;abc&quot;));
     * </code></pre>
     */
    fun isEqualTo(expected: Collection<A>) = PerformableExpectation(value, isEqualTo, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual value is _not_ equal to the given one.
     * <p>
     * Example:
     * <pre><code class='java'> // assertions will pass
     * actor.attemptsTo(Ensure.that(&quot;abc&quot;).isEqualTo(&quot;123&quot;));
     * </code></pre>
     */
    fun isNotEqualTo(expected: Collection<A>) = PerformableExpectation(value, isNotEqualTo, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual value is {@code null}.
     * </code></pre>
     */
    fun isNull() = PerformablePredicate(value, isNullList, isNegated(),targetDescription)

    /**
     * Verifies that the actual value is not {@code null}.
     */
    fun isNotNull() = PerformablePredicate(value, isNotNullList, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} has the expected number of elements
     */
    fun hasSize(expected: Int) = PerformableExpectation(value, hasSize, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} has a size less than the given value.
     */
    fun hasSizeLessThan(expected: Int) = PerformableExpectation(value, hasSizeLessThan, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} has a size less than or equal to the given value.
     */
    fun hasSizeLessThanOrEqualTo(expected: Int) = PerformableExpectation(value, hasSizeLessThanOrEqualTo, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} has a size greater than the given value.
     */
    fun hasSizeGreaterThan(expected: Int) = PerformableExpectation(value, hasSizeGreaterThan, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} has a size greater than the given value.
     */
    fun hasSizeGreaterThanOrEqualTo(expected: Int) = PerformableExpectation(value, hasSizeGreaterThanOrEqualTo, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} has a size between the given boundaries (inclusive)
     * using the {@code length()} method.
     * */
    fun hasSizeBetween(startRange: Int, endRange: Int) = BiPerformableExpectation(value, hasSizeBetween, startRange, endRange, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} has a size that is the same as a specified collection
     * using the {@code length()} method.
     * */
    fun hasSameSizeAs(expected: Collection<A>) = PerformableExpectation(value, hasSameSizeAs, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun contains(vararg expected: A) = PerformableExpectation(value, listContains, expected.toList(), isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun containsAnyOf(vararg expected: A) = PerformableExpectation(value, listContainsAnyOf, expected.toList(), isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun containsAnyElementsOf(expected: Collection<A>) = PerformableExpectation(value, listContainsAnyOf, expected.toList(), isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} contains the given elements
     */
    fun containsElementsFrom(expected: Collection<A>) = PerformableExpectation(value, listContains, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual group contains only the given values and nothing else, in any order and ignoring duplicates (i.e. once a value is found, its duplicates are also considered found).
     */
    fun containsOnly(vararg expected: A) = PerformableExpectation(value, listContainsOnly, expected.toList(), isNegated(),targetDescription)

    /**
     * Verifies that the actual group contains only the given values and nothing else, in any order and ignoring duplicates (i.e. once a value is found, its duplicates are also considered found).
     */
    fun containsOnlyElementsFrom(expected: Collection<A>) = PerformableExpectation(value, listContainsOnly, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} does not contain the given elements
     */
    fun doesNotContain(vararg expected: A) = PerformableExpectation(value, listDoesNotContains, expected.toList(), isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} does not contain the given elements
     */
    fun doesNotContainElementsFrom(expected: Collection<A>) = PerformableExpectation(value, listDoesNotContains, expected, isNegated(),targetDescription)

    /**
     * Verifies that the actual {@code Collection} does not contain the given elements
     */
    fun doesNotHaveDuplicates() = PerformablePredicate(value, listDoesNotContainDuplicates, isNegated(),targetDescription)

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactly(vararg expected: A) = PerformableExpectation(value, listContainsExactly, expected.toList(), isNegated(),targetDescription)

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactlyElementsFrom(expected: Collection<A>) = PerformableExpectation(value, listContainsExactly, expected, isNegated())

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactlyInAnyOrder(vararg expected: A) = PerformableExpectation(value, listContainsExactlyInAnyOrder, expected.toList(), isNegated())

    /**
     * Verifies that the actual group contains exactly the given values and nothing else, <b>in order</b>.<br>
     */
    fun containsExactlyInAnyOrderElementsFrom(expected: Collection<A>) = PerformableExpectation(value, listContainsExactlyInAnyOrder, expected, isNegated())

    /**
     * Verifies that all the elements of actual are present in the given values.
     */
    fun isASubsetOf(vararg expected: A) = PerformableExpectation(value, listIsASubsetOf, expected.toList(), isNegated())

    /**
     * Verifies that all the elements of actual are present in the given values.
     */
    fun isASubsetOf(expected: Collection<A>) = PerformableExpectation(value, listIsASubsetOf, expected, isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun startsWith(vararg expected: A) = PerformableExpectation(value, listStartsWith, expected.toList(), isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun startsWithElementsFrom(expected: Collection<A>) = PerformableExpectation(value, listStartsWith, expected, isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun endsWith(vararg expected: A) = PerformableExpectation(value, listEndsWith, expected.toList(), isNegated())

    /**
     * Verifies that the given {@code Iterable} starts with the given sequence of objects, without any other objects
     */
    fun endsWithElementsFrom(expected: Collection<A>) = PerformableExpectation(value, listEndsWith, expected, isNegated())

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
                    containsElementsThatAllMatch(predicateDescription, predicate),
                    isNegated(),
                    targetDescription)


    fun allMatch(expectation: NamedExpectation<A>) = allMatch(expectation.description, expectation.predicate)

    /**
     * Verifies that at least one element in a collection matches a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun anyMatch(predicateDescription: String, predicate: (A) -> Boolean) =
            // Each element is 4 characters long
            PerformablePredicate(value,
                    containsAtLeastOneElementThatMatches(predicateDescription, predicate),
                    isNegated(),
                    targetDescription)

    fun anyMatch(expectation: NamedExpectation<A>) = anyMatch(expectation.description, expectation.predicate)

    /**
     * Verifies that at least _n_ elements in a collection that match a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun atLeast(n: Int, predicateDescription: String, predicate: (A) -> Boolean): PerformablePredicate<KnowableValue<Collection<A>>> {
        val number: GrammaticalNumber = if (n == 1) SINGULAR else PLURAL
        val elements = if (n == 1) "element" else "elements"
        return PerformablePredicate(value,
                containsAtLeastElementsThatMatch(n, "at least $n $elements",
                        number,
                        IS_ARE,
                        predicateDescription,
                        predicate),
                isNegated(),
                targetDescription)
    }

    fun atLeast(n: Int, expectation: NamedExpectation<A>) = atLeast(n, expectation.description, expectation.predicate)

    /**
     * Verifies that at least _n_ elements in a collection that match a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun exactly(n: Int, predicateDescription: String, predicate: (A) -> Boolean): PerformablePredicate<KnowableValue<Collection<A>>> {
        val number: GrammaticalNumber = if (n == 1) SINGULAR else PLURAL
        val elements = if (n == 1) "element" else "elements"
        return PerformablePredicate(value,
                containsExactlyElementsThatMatch(n, "exactly $n $elements",
                        number,
                        IS_ARE,
                        predicateDescription,
                        predicate),
                isNegated(),
                targetDescription)
    }

    fun exactly(n: Int, expectation: NamedExpectation<A>) = exactly(n, expectation.description, expectation.predicate)

    /**
     * Verifies that no more than _n_ elements in a collection that match a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun noMoreThan(n: Int, predicateDescription: String, predicate: (A) -> Boolean): PerformablePredicate<KnowableValue<Collection<A>>> {
        val number: GrammaticalNumber = if (n == 1) SINGULAR else PLURAL
        val elements = if (n == 1) "element" else "elements"
        return PerformablePredicate(value,
                containsAtMostElementsThatMatch(n, "no more than $n $elements",
                        number,
                        IS_ARE,
                        predicateDescription,
                        predicate),
                isNegated(),
                targetDescription)
    }

    fun noMoreThan(n: Int, expectation: NamedExpectation<A>) = noMoreThan(n, expectation.description, expectation.predicate)

    /**
     * Verifies that at no elements in a collection matches a given predicate
     * @param predicateDescription A short description of the predicate, to appear in error messages
     * @param predicate A predicate operating on the elements of the collection
     */
    fun noneMatch(predicateDescription: String, predicate: (A) -> Boolean) =
            PerformablePredicate(value,
                    containsNoElementsThatMatch(predicateDescription, predicate),
                    isNegated(),
                    targetDescription)

    fun noneMatch(expectation: NamedExpectation<A>) = noneMatch(expectation.description, expectation.predicate)


    private var negated: Boolean = false

    fun isNegated() = negated

    fun not(): CollectionEnsure<A> {
        negated = !negated
        return this
    }

    var silent = false

    override fun isSilent() = silent

    fun silently(): CollectionEnsure<A> {
        silent = true
        return this
    }

    fun usingComparator(comparator: Comparator<A>): CollectionEnsure<A> {
        return CollectionEnsure(value, targetDescription, comparator)
    }

    private val emptyList = expectThatActualIs("empty",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return true
                val actualValue = actual(actor!!) ?: return true
                BlackBox.logAssertion(actualValue, null)
                return actualValue.isEmpty()
            }
    )


    private val notAnEmptyList = expectThatActualIs("not empty",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, null)
                return actualValue.isNotEmpty()
            }
    )
    private val nullOrEmptyList = expectThatActualIs("null or empty",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return true
                val actualValue = actual(actor!!) ?: return true
                return actualValue.isEmpty()
            })

    private val isNotNullList = expectThatActualIs("not null",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!)
                BlackBox.logAssertion(actualValue, null)
                return actualValue != null
            })
    private val isNullList = expectThatActualIs("null",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean { return (actual == null) || (actual(actor!!) == null) }
    )

    private val isEqualTo = expectThatActualIs("equal to",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!)
                return CollectionsComparison(comparator).areEqual(actualValue, expected)
            }
    )
    private val isNotEqualTo = expectThatActualIs("not equal to",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!)
                BlackBox.logAssertion(actualValue, expected)
                return !CollectionsComparison(comparator).areEqual(actualValue, expected)
            }
    )

    private fun plural(n : Int) = if (n == 1) "" else "s"

    private val hasSize = expectThatActualIs("of size",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Int): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion("a list with ${actualValue.size} element${plural(actualValue.size)} containing $actualValue", expected)
                return actualValue.size == expected
            }
    )

    private val hasSizeLessThan = expectThatActualIs("of size less than",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Int): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion("a list with ${actualValue.size} element${plural(actualValue.size)} containing $actualValue", expected)
                return actualValue.size < expected
            }
    )

    private val hasSizeLessThanOrEqualTo = expectThatActualIs("of size less than or equal to",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Int): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion("a list with ${actualValue.size} element${plural(actualValue.size)} containing $actualValue", expected)
                return actualValue.size <= expected
            }
    )

    private val hasSizeGreaterThan = expectThatActualIs("of size greater than",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Int): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion("a list with ${actualValue.size} element${plural(actualValue.size)} containing $actualValue", expected)
                return actualValue.size > expected
            }
    )

    private val hasSizeGreaterThanOrEqualTo = expectThatActualIs("of size greater than or equal to",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Int): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion("a list with ${actualValue.size} element${plural(actualValue.size)} containing $actualValue", expected)
                return actualValue.size >= expected
            }
    )

    private val hasSizeBetween = expectThatActualIs("of size of between",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, startRange: Int, endRange: Int): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion("a list with ${actualValue.size} element${plural(actualValue.size)} containing $actualValue", null)
                return actualValue.size in startRange..endRange
            })

    private val hasSameSizeAs = expectThatActualIs("the same size as",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
                if (actual == null || expected == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion("a list with ${actualValue.size} element${plural(actualValue.size)} containing $actualValue", expected)
                return actualValue.size == expected.size
            })

    private val listContains = expectThatActualIs("contains", "does not contain",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
                if (actual == null || expected == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, expected)
                return actualValue.containsAll(expected)
            })

    private val listDoesNotContains = expectThatActualIs("does not contain","contains",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
                if (actual == null || expected == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, expected)
                return !actualValue.containsAll(expected)
            })

    private val listContainsOnly = expectThatActualIs("contains only","does not contain only",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
                if (actual == null || expected == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, expected)
                return actualValue.all { expected.contains(it) }
            })

    private val listContainsAnyOf = expectThatActualIs("contains any of","does not contain any of",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
                if (actual == null || expected == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, expected)
                return (expected.find { actualValue.contains(it) } != null)
            })


    private val listContainsExactly = expectThatActualIs("contains exactly", "does not contain exactly",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false
        val actualValue = actual(actor!!) ?: return false
        BlackBox.logAssertion(actualValue, expected)
        return (actualValue == expected)
    })

    private val listContainsExactlyInAnyOrder = expectThatActualIs("contains exactly in any order", "does not contain exactly in any order",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
                if (actual == null || expected == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, expected)
                return (actualValue.toSet() == expected.toSet())
            })

    private val listIsASubsetOf = expectThatActualIs("a subset of", fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false
        val actualValue = actual(actor!!) ?: return false
        BlackBox.logAssertion(actualValue, expected)
        return (actualValue.all { element -> expected.contains(element) })
    })

    private val listDoesNotContainDuplicates = expectThatActualIs("contains no duplicates","contains duplicates", fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
        if (actual == null) return false
        val actualValue = actual(actor!!) ?: return false
        BlackBox.logAssertion(actualValue, null)
        return actualValue.toSet().size == actualValue.size
    })

    private val listStartsWith = expectThatActualIs("starts with","does not start with", fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false
        val actualValue = actual(actor!!) ?: return false
        if (actualValue.size <= expected.size || expected.isEmpty()) return false
        BlackBox.logAssertion(actualValue, expected)
        return sequencesAreIdenticalIn(actualValue, expected, 0, expected.size - 1)
    })

    private val listEndsWith = expectThatActualIs("ends with", "does not end with", fun(actor: Actor?, actual: KnowableValue<Collection<A>>?, expected: Collection<A>?): Boolean {
        if (actual == null || expected == null) return false
        val actualValue = actual(actor!!) ?: return false
        if (actualValue.size <= expected.size || expected.isEmpty()) return false
        BlackBox.logAssertion(actualValue, expected)
        val sequenceEnd = actualValue.size - 1
        val sequenceStart = sequenceEnd - expected.size + 1
        return sequencesAreIdenticalIn(actualValue, expected, sequenceStart, sequenceEnd)
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

    private fun containsElementsThatAllMatch(predicateDescription: String,
                                             predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            "each element",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, null)
                return actualValue.all(predicate)
            },
            predicateDescription,
            IS_ARE,
            SINGULAR
    )

    private fun containsAtLeastOneElementThatMatches(predicateDescription: String,
                                                     predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            "at least one element",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, null)
                return actualValue.any(predicate)
            },
            predicateDescription,
            IS_ARE,
            SINGULAR
    )

    private fun containsNoElementsThatMatch(predicateDescription: String,
                                            predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            "no elements",
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, null)
                return actualValue.none(predicate)
            },
            predicateDescription,
            IS_ARE,
            PLURAL
    )

    private fun containsAtLeastElementsThatMatch(n: Int,
                                                 overallDescription: String,
                                                 predicateNumber: GrammaticalNumber,
                                                 qualifier: ElementQualifier,
                                                 predicateDescription: String,
                                                 predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            overallDescription,
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, null)
                return actualValue.filter(predicate).size >= n
            },
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
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, null)
                return actualValue.filter(predicate).size <= n
            },
            predicateDescription,
            qualifier,
            predicateNumber
    )

    private fun containsExactlyElementsThatMatch(n: Int,
                                                overallDescription: String,
                                                predicateNumber: GrammaticalNumber,
                                                qualifier: ElementQualifier,
                                                predicateDescription: String,
                                                predicate: (A) -> Boolean) = expectThatActualContainsElementsThat(
            overallDescription,
            fun(actor: Actor?, actual: KnowableValue<Collection<A>>?): Boolean {
                if (actual == null) return false
                val actualValue = actual(actor!!) ?: return false
                BlackBox.logAssertion(actualValue, null)
                return actualValue.filter(predicate).size == n
            },
            predicateDescription,
            qualifier,
            predicateNumber
    )
}

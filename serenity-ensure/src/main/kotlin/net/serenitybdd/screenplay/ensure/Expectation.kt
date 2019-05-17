package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor

typealias BooleanPredicate<A> = (actor: Actor?, actual: A) -> Boolean
typealias SingleValuePredicate<A, E> = (actor: Actor?, actual: A, expected: E) -> Boolean
typealias DoubleValuePredicate<A, E> = (actor: Actor?, actual: A, startRange: E, endRange: E) -> Boolean

fun <A, E> expectThatActualIs(message: String, predicate: SingleValuePredicate<A, E>) = Expectation(message, predicate)
fun <A, E> expectThatActualIs(message: String, predicate: DoubleValuePredicate<A, E>) = DoubleValueExpectation(message, predicate)
fun <A> expectThatActualIs(message: String, predicate: BooleanPredicate<A>) = PredicateExpectation(message, predicate)
fun <A, E> expectThatActualIs(message: String, predicate: SingleValuePredicate<A, E>, predicateDescription: String) = Expectation(message, predicate, predicateDescription)
fun <A> expectThatActualContainsElementsThat(message: String,
                                             predicate: BooleanPredicate<A>,
                                             predicateDescription: String,
                                             qualifier: ElementQualifier,
                                             predicateNumber: GrammaticalNumber) =
        CollectionPredicateExpectation(message,
                predicateDescription,
                qualifier,
                predicate,
                predicateNumber)

/**
 * Models a single value predicate with a description
 */
class Expectation<A, E>(private val expectation: String,
                        val predicate: SingleValuePredicate<A, E>,
                        val readableExpectedValue: String? = null) {
    fun describe(actual: A?, expected: E, isNegated: Boolean = false) = comparisonDescription(expectation, actual as Any?, expected as Any?, isNegated, readableExpectedValue)
    fun apply(actual: A, expected: E, actor: Actor?) = predicate(actor, actual, expected)
}

/**
 * Models a value predicate with a description that takes no parameters
 */
open class PredicateExpectation<A>(val expectation: String, val predicate: BooleanPredicate<A>, val isNegated: Boolean = false) {
    open fun describe(actual: A?, isNegated: Boolean = false) = predicateDescription(expectation, actual as Any?, isNegated)
    fun apply(actual: A, actor: Actor?) = if (isNegated) !predicate(actor, actual) else predicate(actor, actual)
}

class CollectionPredicateExpectation<A>(expectation: String,
                                        val predicateDescription: String,
                                        val qualifier: ElementQualifier,
                                        predicate: BooleanPredicate<A>,
                                        val number: GrammaticalNumber) : PredicateExpectation<A>(expectation, predicate) {
    override fun describe(actual: A?, isNegated: Boolean) = collectionPredicateDescription(
            expectation,
            qualifier,
            predicateDescription,
            number,
            actual as Any?,
            isNegated)
}

/**
 * Models a value predicate with a description that takes two parameters
 */
class DoubleValueExpectation<A, E>(val expectation: String, val predicate: DoubleValuePredicate<A, E>) {
    fun describe(actual: A, startRange: E, endRange: E, isNegated: Boolean = false) = rangeDescription(expectation,
            actual,
            startRange as Any,
            endRange as Any,
            isNegated)

    fun apply(actual: A, startRange: E, endRange: E, actor: Actor?) = predicate(actor, actual, startRange, endRange)
}


private fun comparisonDescription(expectation: String, actual: Any?,
                                  expected: Any?,
                                  isNegated: Boolean,
                                  readableExpectedValue: String?): String {
    val expectedDescription = "Expecting ${expectedType(actual)} ${thatIsOrIsnt(isNegated)}$expectation"
    val butGot = "But got".padEnd(expectedDescription.length, '.')
    val expectedValue = if (readableExpectedValue != null) readableExpectedValue else expectedValue(expected)

    return """
$expectedDescription: $expectedValue
$butGot: ${actualValue(actual)}"""
}

private fun rangeDescription(expectation: String, actual: Any?, startRange: Any, endRange: Any, isNegated: Boolean): String {
    val expectedDescription = "Expecting ${expectedType(actual)} ${thatIsOrIsnt(isNegated)}$expectation ${expectedValue(startRange)} and ${expectedValue(endRange)}"

    return """
$expectedDescription
But got: ${actualValue(actual)}"""
}


private fun predicateDescription(expectation: String, actual: Any?, isNegated: Boolean): String {
    val expectedDescription = "Expecting ${expectedType(actual)} ${thatIsOrIsnt(isNegated)}$expectation"

    return """
$expectedDescription
But got: ${actualValue(actual)}"""
}

private fun collectionPredicateDescription(expectation: String,
                                           qualifier: ElementQualifier,
                                           predicateDescription: String,
                                           number: GrammaticalNumber,
                                           actual: Any?,
                                           isNegated: Boolean): String {
    // Expecting a collection that [matches|does not match]: [all elements|no element] [have size|has size] [greater than 4]
    val qualifier = qualifier.resolve(number)
    val matchesOrDoesNotMatch = matchesOrDoesNotMatch(isNegated)
    val expectedDescription = "Expecting a collection that $matchesOrDoesNotMatch: $expectation $qualifier $predicateDescription"

    return """
$expectedDescription
But got: ${actualValue(actual)}"""
}

fun expectedType(expected: Any?) = if (expected is Collection<*>) "a collection" else "a value"
fun thatIsOrIsnt(isNegated: Boolean) = if (isNegated) "that is not " else "that is "
fun matchesOrDoesNotMatch(isNegated: Boolean) = if (isNegated) "does not match" else "matches"
fun expectedValue(expected: Any?) = if (expected is String) "<\"$expected\">" else "<$expected>"
fun actualValue(actual: Any?) = if (actual is String) "<\"$actual\">" else "<$actual>"

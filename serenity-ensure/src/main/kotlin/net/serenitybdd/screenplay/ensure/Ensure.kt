@file:JvmName("Ensure")

package net.serenitybdd.screenplay.ensure

import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.AnonymousPerformableFunction
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.ensure.web.PageObjectEnsure
import net.serenitybdd.screenplay.ensure.web.TargetEnsure
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By
import java.time.LocalDate
import java.time.LocalTime
import java.util.function.Consumer

fun that(value: String?) = StringEnsure(value)
fun that(value: LocalDate?) = DateEnsure(value)
fun that(value: LocalTime?) = TimeEnsure(value)
fun that(value: Boolean?) = BooleanEnsure(value)
fun that(value: Float?) = FloatEnsure(value)
fun that(value: Double?) = DoubleEnsure(value)
fun <A> that(value: Comparable<A>) = ComparableEnsure(value)
fun <A> that(value: Collection<A>?) = CollectionEnsure(value)

@Deprecated("Use that() instead")
fun <A : Boolean?> thatTheAnswerTo(description: String, question: Question<Boolean?>) =
    BooleanEnsure(KnowableBooleanAnswer(question, description))

fun <A : Boolean?> that(description: String, question: Question<Boolean?>) =
    BooleanEnsure(KnowableBooleanAnswer(question, description))

@Deprecated("Use that() instead")
fun <A : Boolean?> thatTheAnswerTo(question: Question<Boolean?>) =
    BooleanEnsure(KnowableBooleanAnswer(question, question.subject))

fun <A : Boolean?> that(question: Question<Boolean?>) = BooleanEnsure(KnowableBooleanAnswer(question, question.subject))

fun <A> that(question: Question<A>, predicate: (actual: A) -> Boolean) = that("predicate check at ${predicateLocation()}", question, predicate)
fun <A> that(description: String, question: Question<A>, predicate: (actual: A) -> Boolean) =
    AnonymousPerformableFunction("Ensure that $description",
        object : Consumer<Actor> {
            override fun accept(actor: Actor) {
                val actual = question.answeredBy(actor)
                if (!predicate.invoke(actual)) {
                    throw AssertionError("Expected $description but was $actual")
                }
            }
        }
    )

fun predicateLocation() : String {
    val className = Throwable().stackTrace[2].className
    val methodName = Throwable().stackTrace[2].methodName
    val lineNumber = Throwable().stackTrace[2].lineNumber
    return "$className.$methodName (line $lineNumber)"
}

@Deprecated("Use that() instead")
fun <A : String?> thatTheAnswerTo(description: String, question: Question<String?>) =
    StringEnsure(KnowableStringAnswer(question, description))

fun <A : String?> that(description: String, question: Question<String?>) =
    StringEnsure(KnowableStringAnswer(question, description))

@Deprecated("Use that() instead")
fun <A : String?> thatTheAnswerTo(question: Question<String?>) =
    StringEnsure(KnowableStringAnswer(question, question.subject))

fun <A : String?> that(question: Question<String?>) = StringEnsure(KnowableStringAnswer(question, question.subject))

@Deprecated("Use that() instead")
fun <A : Comparable<A>> thatTheAnswerTo(description: String, question: Question<A>) =
    ComparableEnsure(KnowableComparableAnswer(question, description), null, description)

fun <A : Comparable<A>> that(description: String, question: Question<A>) =
    ComparableEnsure(KnowableComparableAnswer(question, description), null, description)

@Deprecated("Use that() instead")
fun <A : Comparable<A>> thatTheAnswerTo(question: Question<A>) = that(question.subject, question)
fun <A : Comparable<A>> that(question: Question<A>) = that(question.subject, question)

@Deprecated("Use that() instead")
fun <A> thatTheAnswersTo(description: String, question: Question<Collection<A>>) =
    CollectionEnsure(KnowableCollectionAnswer(question, description), description)

fun <A> that(description: String, question: Question<Collection<A>>) =
    CollectionEnsure(KnowableCollectionAnswer(question, description), description)

@Deprecated("Use that() instead")
fun <A> thatTheAnswersTo(question: Question<Collection<A>>) = that(question.subject, question)
fun <A> that(question: Question<Collection<A>>) = that(question.subject, question)

fun <A> thatTheListOf(description: String, question: Question<List<A>>) =
    CollectionEnsure(KnowableListAnswer(question, description), description)

fun <A> thatTheListOf(question: Question<List<A>>) =
    thatTheListOf(question.subject, question)

fun thatTheCurrentPage() = PageObjectEnsure()
fun that(value: Target) = TargetEnsure(value)
fun that(value: By) = TargetEnsure(value)

// Collection matchers
fun thatTheListOf(value: Target) =
    CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")

fun thatTheListOf(value: By) =
    CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")

fun thatAmongst(value: Target) =
    CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")

fun thatAmongst(value: By) =
    CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")

fun enableSoftAssertions() = BlackBox.startSoftAssertions()
fun reportSoftAssertions() = BlackBox.reportAnySoftAssertions()

///**
// * Ensure something
// */
//fun that(description: String, check: Consumer<ValidatableResponse>) = net.serenitybdd.rest.Ensure.that(description, check)


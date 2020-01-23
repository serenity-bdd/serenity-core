@file:JvmName("Ensure")

package net.serenitybdd.screenplay.ensure

import net.serenitybdd.core.pages.PageObject
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.ensure.web.PageObjectEnsure
import net.serenitybdd.screenplay.ensure.web.TargetEnsure
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By
import java.time.LocalDate
import java.time.LocalTime

fun that(value: String?) = StringEnsure(value)
fun that(value: LocalDate?) = DateEnsure(value)
fun that(value: LocalTime?) = TimeEnsure(value)
fun that(value: Boolean?) = BooleanEnsure(value)
fun that(value: Float?) = FloatEnsure(value)
fun that(value: Double?) = DoubleEnsure(value)
fun <A> that(value: Comparable<A>) = ComparableEnsure(value)
fun <A> that(value: Collection<A>?) = CollectionEnsure(value)

fun <A : Boolean?> thatTheAnswerTo(description: String, question: Question<Boolean?>) = BooleanEnsure(KnowableBooleanAnswer(question, description))

fun <A : Boolean?> thatTheAnswerTo(question: Question<Boolean?>) = BooleanEnsure(KnowableBooleanAnswer(question, question.subject))

fun <A : String?> thatTheAnswerTo(description: String, question: Question<String?>) = StringEnsure(KnowableStringAnswer(question, description))

fun <A : String?> thatTheAnswerTo(question: Question<String?>) = StringEnsure(KnowableStringAnswer(question, question.subject))

fun <A : Comparable<A>> thatTheAnswerTo(description: String, question: Question<A>) =
        ComparableEnsure(KnowableComparableAnswer(question, description), null, description)

fun <A : Comparable<A>> thatTheAnswerTo(question: Question<A>) = thatTheAnswerTo(question.subject, question)

fun <A> thatTheAnswersTo(description: String, question: Question<Collection<A>>) =
        CollectionEnsure(KnowableCollectionAnswer(question, description), description)

fun <A> thatTheAnswersTo(question: Question<Collection<A>>) = thatTheAnswersTo(question.subject, question)

fun thatTheCurrentPage() = PageObjectEnsure()
fun that(value: Target) = TargetEnsure(value)
fun that(value: By) = TargetEnsure(value)

// Collection matchers
fun thatTheSetOf(value: Target) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")
fun thatTheSetOf(value: By) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")
fun thatAmongst(value: Target) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")
fun thatAmongst(value: By) = CollectionEnsure(KnowableCollectionTarget(value), "a collection of ${KnowableCollectionTarget(value)}")

///**
// * Ensure something
// */
//fun that(description: String, check: Consumer<ValidatableResponse>) = net.serenitybdd.rest.Ensure.that(description, check)

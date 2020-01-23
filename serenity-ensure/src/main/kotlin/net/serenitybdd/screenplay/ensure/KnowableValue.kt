package net.serenitybdd.screenplay.ensure

import net.serenitybdd.core.pages.WebElementFacade
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Question
import net.serenitybdd.screenplay.questions.EnumValues
import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


typealias KnowableValue<A> = (Actor) -> A?

class KnowableStringValue<A>(val value : KnowableValue<A>, val description: String) : KnowableValue<String?> {
    override fun invoke(actor: Actor) = value(actor).toString()
    override fun toString() = description
}

class KnowableIntValue<A>(val value : KnowableValue<A>, val description: String) : KnowableValue<Int?> {
    override fun invoke(actor: Actor)= value(actor).toString().toInt()
    override fun toString() = description
}

class KnowableDoubleValue<A>(val value : KnowableValue<A>, val description: String) : KnowableValue<Double?> {
    override fun invoke(actor: Actor) = value(actor).toString().toDouble()
    override fun toString() = description
}

class KnowableFloatValue<A>(val value : KnowableValue<A>, val description: String) : KnowableValue<Float?> {
    override fun invoke(actor: Actor) = value(actor).toString().toFloat()
    override fun toString() = description
}

class KnowableBigDecimalValue<A>(val value : KnowableValue<A>, val description: String) : KnowableValue<BigDecimal?> {
    override fun invoke(actor: Actor) = value(actor).toString().toBigDecimal()
    override fun toString() = description
}

class KnowableLocalDateValue<A>(val value : KnowableValue<A>, val description: String, val format: String? = null) : KnowableValue<LocalDate?> {
    override fun invoke(actor: Actor): LocalDate? {
        val resolvedValue = value(actor)
        return if (resolvedValue is LocalDate) resolvedValue else parsedDateFor(resolvedValue.toString())
    }
    override fun toString() = description

    private fun parsedDateFor(dateValue: String) : LocalDate {
        val formatter = if (format != null) DateTimeFormatter.ofPattern(format) else DateTimeFormatter.ISO_LOCAL_DATE
        return LocalDate.parse(dateValue, formatter)
    }
}

class KnowableLocalTimeValue<A>(val value : KnowableValue<A>, val description: String, val format: String? = null) : KnowableValue<LocalTime?> {
    override fun invoke(actor: Actor): LocalTime? {
        val resolvedValue = value(actor)
        return if (resolvedValue is LocalTime) resolvedValue else parsedTimeFor(resolvedValue.toString())
    }
    override fun toString() = description

    private fun parsedTimeFor(dateValue: String) : LocalTime {
        val formatter = if (format != null) DateTimeFormatter.ofPattern(format) else DateTimeFormatter.ISO_LOCAL_TIME
        return LocalTime.parse(dateValue, formatter)
    }
}

class KnowableBooleanValue<A>(val value : KnowableValue<A>, val description: String) : KnowableValue<Boolean?> {
    override fun invoke(actor: Actor) = value(actor).toString().toBoolean()
    override fun toString() = description
}

class KnownValue<A>(val value: A?, val description: String) : KnowableValue<A> {
    override fun invoke(actor: Actor): A? = value
    override fun toString() = description
}

interface HasSubject {
    fun subject() : String
}

class KnowableAnswer<A>(val question: Question<A>, val description: String) : KnowableValue<A>, HasSubject {
    override fun invoke(actor: Actor): A? = question.answeredBy(actor)
    override fun toString() = description
    override fun subject() = question.subject
}

class KnowableStringAnswer(val question: Question<String?>, val description: String) : KnowableValue<String?>, HasSubject {
    override fun invoke(actor: Actor): String? = question.answeredBy(actor)
    override fun toString() = description
    override fun subject() = question.subject
}

class KnowableBooleanAnswer(val question: Question<Boolean?>, val description: String) : KnowableValue<Boolean?>, HasSubject {
    override fun invoke(actor: Actor): Boolean? = question.answeredBy(actor)
    override fun toString() = description
    override fun subject() = question.subject
}

class KnowableComparableAnswer<A>(val question: Question<A>, val description: String) : KnowableValue<A>, HasSubject {
    override fun invoke(actor: Actor): A = question.answeredBy(actor)
    override fun toString() = description
    override fun subject() = question.subject
}

class KnowableCollectionAnswer<A>(val question: Question<Collection<A>>, val description: String) : KnowableValue<Collection<A>>, HasSubject {
    override fun invoke(actor: Actor): Collection<A> = question.answeredBy(actor)
    override fun toString() = description
    override fun subject() = question.subject
}

class KnowableCollectionTarget(val target: Target, val description: String = pluralFormOf(target.name)) : KnowableValue<List<WebElementFacade>?> {

    constructor(locator: By) : this(Target.the(locator.toString()).located(locator))

    override fun invoke(actor: Actor): List<WebElementFacade> = target.resolveAllFor(actor)
    override fun toString() = description
}
package net.serenitybdd.reports.model

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.NameConverter.humanize

/**
 * Custom report fields are stored in the serenity.properties file as report.customfields.* properties, or passed in on the command line.
 * They can be ordered by providing the list of fields in the report.customfields.order field.
 *
 *
 */
class CustomReportFields(val environmentVariables: EnvironmentVariables) {

    companion object {
        const val CUSTOM_FIELDS_PREFIX = "report.customfields"
        const val CUSTOM_FIELD_ORDER = "report.customfields.order"
    }

    private val orderedFields: List<String> = orderedFields(environmentVariables).map { field -> "report.customfields." + field }
    private val fields: List<String> = customFieldsDefinedIn(environmentVariables)
    val fieldNames: List<String> = customFieldOrderDefinedIn(fields, environmentVariables)
    val values: List<String> = customFieldValueDefinedIn(fields, environmentVariables)
    private val env
        get() = environmentVariables.getProperty("environment", "default")
    private val fieldsRegex
        get() = "($CUSTOM_FIELDS_PREFIX|environments\\.(all|$env)\\.$CUSTOM_FIELDS_PREFIX).*".toRegex()
    private val orderedFieldsRegex
        get() = "($CUSTOM_FIELD_ORDER|environments\\.(all|$env)\\.$CUSTOM_FIELD_ORDER).*".toRegex()

    private fun customFieldsDefinedIn(environmentVariables: EnvironmentVariables): List<String> {

        return if (orderedFields.isNotEmpty()) {
            orderedFields
        } else {
            environmentVariables
                    .keys
                    .filter { key -> key.matches(fieldsRegex) }
        }
    }

    private fun customFieldValueDefinedIn(fields: List<String>, environmentVariables: EnvironmentVariables): List<String> =
            fields.map { key ->
                environmentVariables.injectSystemPropertiesInto(environmentVariables.getProperty(key)) ?: ""
            }


    private fun customFieldOrderDefinedIn(fields: List<String>, environmentVariables: EnvironmentVariables): List<String> {
        val predefinedFields = orderedFields(environmentVariables)
        return if (predefinedFields.isNotEmpty()) {
            predefinedFields.map { field -> humanize(field) }
        } else {
            fields.map { fieldName -> humanReadableLabelOf(fieldName) }
        }
    }

    private fun humanReadableLabelOf(field: String): String = humanize(field.substringAfter("$CUSTOM_FIELDS_PREFIX."))

    private fun orderedFields(environmentVariables: EnvironmentVariables): List<String> = environmentVariables
            .keys
            .firstOrNull { key ->
                key.matches(orderedFieldsRegex)
            }?.let { key ->
                environmentVariables.getProperty(key).split(",").map { field -> field.trim() }
            } ?: emptyList()

}
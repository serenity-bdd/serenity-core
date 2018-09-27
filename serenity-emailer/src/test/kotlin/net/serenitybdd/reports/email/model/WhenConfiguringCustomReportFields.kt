package net.serenitybdd.reports.email.model

import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.util.SystemEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenConfiguringCustomReportFields {

    @Test
    fun `custom fields are provided in the serenity configuration file under report-dot-customfields`() {
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.color","Red")
        environmentVariables.setProperty("report.customfields.food","Apples")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactlyInAnyOrder("Color","Food")
        assertThat(customReportFields.values).containsExactlyInAnyOrder("Red","Apples")
    }

    @Test
    fun `the order of custom fields can be mandated by using the report-dot-customfields-dot-order property`() {
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.color","Red")
        environmentVariables.setProperty("report.customfields.food","Apples")
        environmentVariables.setProperty("report.customfields.drink","Cidre")
        environmentVariables.setProperty("report.customfields.order","food,drink,color")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("Food","Drink","Color")
        assertThat(customReportFields.values).containsExactly("Apples","Cidre","Red")
    }

    @Test
    fun `incorrect fields in the order configuration should be ignored`() {
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.color","Red")
        environmentVariables.setProperty("report.customfields.food","Apples")
        environmentVariables.setProperty("report.customfields.drink","Cidre")
        environmentVariables.setProperty("report.customfields.order","food,drink,number,color")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("Food","Drink","Number","Color")
        assertThat(customReportFields.values).containsExactly("Apples","Cidre","","Red")
    }

    @Test
    fun `field values can be defined from system properties using the dollar notation`() {
        System.setProperty("COLOR","blue")
        val environmentVariables = SystemEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.color","\${HOME}")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("Color")
        assertThat(File(customReportFields.values[0])).exists()
    }


    @Test
    fun `field names can be acronyms`() {
        val environmentVariables = SystemEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.XYZ","123")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("XYZ")
    }

}